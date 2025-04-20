package com.gewuyou.baseforge.autoconfigure.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gewuyou.baseforge.autoconfigure.util.exception.UtilException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Bean 复制工具
 *
 * @author gewuyou
 * @since 2024-10-20 10:28:11
 */
@Slf4j
@Component
public class BeanCopyUtil extends BeanUtils {
    private final ObjectMapper objectMapper;

    @Autowired
    public BeanCopyUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 将源对象的属性浅拷贝到目标类的新实例中
     *
     * @param source 要从中复制的对象
     * @param target 要创建并复制到的对象的类
     * @param <T>    目标对象的类型
     * @return 具有 Copy 属性的目标类的新实例
     * @apiNote 该方法使用 Spring 的 BeanUtils.copyProperties 方式进行属性赋值，不建议用来拷贝相同对象或者被拷贝对象会发生变化的情况。
     */
    public static <T> Optional<T> shallowCopyObject(Object source, Class<T> target) {
        T temp = null;
        try {
            temp = target.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            log.error("无法实例化目标对象，请检查类是否有默认构造函数。", e);
        } catch (IllegalAccessException e) {
            log.error("没有权限访问目标对象", e);
        } catch (InvocationTargetException e) {
            log.error("调用目标对象方法或构造函数时发生异常", e);
        } catch (NoSuchMethodException e) {
            log.error("找不到指定方法", e);
        }
        if (Objects.nonNull(source) && Objects.nonNull(temp)) {
            copyProperties(source, temp);
        }
        return Optional.ofNullable(temp);
    }

    /**
     * 将对象列表从源列表复制到新的目标对象列表。
     *
     * @param source 要从中复制的源对象列表
     * @param target 要复制到的目标对象的类
     * @param <T>    目标对象的类型
     * @param <S>    源对象的类型
     * @return 从源列表中复制的目标对象的新列表
     */
    public static <T, S> List<T> shallowCopyList(List<S> source, Class<T> target) {
        List<T> list = new ArrayList<>();
        if (Objects.nonNull(source) && !source.isEmpty()) {
            for (Object obj : source) {
                Optional<T> t = BeanCopyUtil.shallowCopyObject(obj, target);
                list.add(t.orElseThrow(() -> new UtilException("无法复制对象")));
            }
        }
        return list;
    }

    /**
     * 浅拷贝对象属性，并对属性值进行校验
     *
     * @param source           源对象
     * @param target           目标对象class
     * @param validator        属性值校验器
     * @param ignoreProperties 需要忽略的属性列表
     * @param <T>              目标对象的类型
     * @return 具有 Copy 属性的目标类的新实例
     * @throws Exception 异常
     */
    public static <T> T copyPropertiesWithValidation(Object source, Class<T> target,
                                                     PropertyValidator validator,
                                                     String... ignoreProperties) {
        try {
            T temp = target.getDeclaredConstructor().newInstance();
            copyPropertiesWithValidation(source, temp, validator, ignoreProperties);
            return temp;
        } catch (Exception e) {
            log.error("复制对象属性失败", e);
            throw new UtilException("复制对象属性失败");
        }
    }

    /**
     * 浅拷贝对象属性，并对属性值进行校验
     *
     * @param source           源对象
     * @param target           目标对象
     * @param validator        属性值校验器
     * @param ignoreProperties 需要忽略的属性列表
     * @throws IntrospectionException 异常
     */
    public static void copyPropertiesWithValidation(Object source, Object target,
                                                    PropertyValidator validator,
                                                    String... ignoreProperties) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        if (Objects.isNull(source) || Objects.isNull(target)) {
            throw new IllegalArgumentException("Source and Target must not be null");
        }
        // 获取目标对象的所有属性描述符
        Map<String, PropertyDescriptor> targetPropertyDescriptors = Arrays.stream(Introspector
                        .getBeanInfo(target.getClass())
                        .getPropertyDescriptors())
                .collect(
                        Collectors.toMap(PropertyDescriptor::getName, p -> p)
                );
        // 获取源对象的所有属性描述符
        Map<String, PropertyDescriptor> sourcePropertyDescriptors =
                Arrays.stream(Introspector
                                .getBeanInfo(source.getClass())
                                .getPropertyDescriptors())
                        .collect(
                                Collectors.toMap(PropertyDescriptor::getName, p -> p)
                        );
        // 将需要忽略的属性转换为Set，方便后续检查
        Set<String> ignoreList = Objects.nonNull(ignoreProperties) ? Set.of(ignoreProperties) : null;
        copy(source, target, validator, targetPropertyDescriptors, ignoreList, sourcePropertyDescriptors);
    }

    /**
     * 拷贝对象属性，并对属性值进行校验
     *
     * @param source                    源对象
     * @param target                    目标对象class
     * @param validator                 属性值校验器
     * @param targetPropertyDescriptors 目标属性描述符
     * @param ignoreList                需要忽略的属性列表
     * @param sourcePropertyDescriptors 源属性描述符
     * @throws IllegalAccessException    异常
     * @throws InvocationTargetException 异常
     */
    private static void copy(Object source, Object target, PropertyValidator validator, Map<String, PropertyDescriptor> targetPropertyDescriptors, Set<String> ignoreList, Map<String, PropertyDescriptor> sourcePropertyDescriptors) throws IllegalAccessException, InvocationTargetException {
        for (Map.Entry<String, PropertyDescriptor> targetPropertyDescriptorEntry : targetPropertyDescriptors.entrySet()) {
            // 获取目标属性的写方法
            Method writeMethod = targetPropertyDescriptorEntry.getValue().getWriteMethod();
            // 检查读方法不为空
            if (Objects.nonNull(writeMethod) &&
                    (Objects.isNull(ignoreList) ||
                            !ignoreList.contains(targetPropertyDescriptorEntry.getKey()))) {
                copy(source, target, validator, targetPropertyDescriptorEntry, sourcePropertyDescriptors, writeMethod);
            }
        }
    }

    /**
     * 拷贝对象属性，并对属性值进行校验
     *
     * @param source                        源对象
     * @param target                        目标对象
     * @param validator                     属性值校验器
     * @param targetPropertyDescriptorEntry 目标属性描述符
     * @param sourcePropertyDescriptors     源属性描述符
     * @param writeMethod                   目标属性的写方法
     * @throws IllegalAccessException    异常
     * @throws InvocationTargetException 异常
     */
    private static void copy(Object source, Object target, PropertyValidator validator, Map.Entry<String, PropertyDescriptor> targetPropertyDescriptorEntry, Map<String, PropertyDescriptor> sourcePropertyDescriptors, Method writeMethod) throws IllegalAccessException, InvocationTargetException {
        // 尝试获取源对象的属性描述符
        PropertyDescriptor sourcePropertyDescriptor = sourcePropertyDescriptors.get(targetPropertyDescriptorEntry.getKey());
        if (Objects.isNull(sourcePropertyDescriptor)) {
            return;
        }
        // 获取源属性的读方法
        Method readMethod = sourcePropertyDescriptor.getReadMethod();
        if (Objects.isNull(readMethod)) {
            return;
        }
        Object value = readMethod.invoke(source);
        // 如果不符合条件或者类型不匹配，则跳过
        if (Stream.of(validator.isValid(value) &&
                (writeMethod.getParameterTypes()[0].isInstance(value) ||
                        writeMethod.getParameterTypes()[0].equals(value.getClass()))).allMatch(p -> p.equals(true))) {
            // 调用目标对象的写方法并设置值
            writeMethod.invoke(target, value);
        }
    }

    /**
     * 将源对象属性深拷贝到目标类的新实例中
     *
     * @param source 要从中复制的对象
     * @param target 要创建并复制到的对象的类
     * @param <T>    目标对象的类型
     * @return 具有 Copy 属性的目标类的新实例
     * @apiNote 该方法使用 Jackson 的 ObjectMapper 进行属性赋值，建议用来拷贝相同对象或者被拷贝的对象可能会发生变化的情况。
     */
    public <T> Optional<T> deepCopyObject(Object source, Class<T> target) {
        try {
            String jsonStr = objectMapper.writeValueAsString(source);
            return Optional.of(objectMapper.readValue(jsonStr, target));
        } catch (JsonMappingException e) {
            log.error("Json映射异常，请检查源对象属性是否符合目标对象属性。", e);
        } catch (JsonProcessingException e) {
            log.error("Json序列化异常，请检查源对象属性是否符合目标对象属性。", e);
        }
        return Optional.empty();
    }

    /**
     * 将对象列表从源列表复制到新的目标对象列表。
     *
     * @param source 要从中复制的源对象列表
     * @param target 要复制到的目标对象的类
     * @param <T>    目标对象的类型
     * @param <S>    源对象的类型
     * @return 从源列表中复制的目标对象的新列表
     */
    public <T, S> List<T> deepCopyList(List<S> source, Class<T> target) {
        List<T> list = new ArrayList<>();
        if (Objects.nonNull(source) && !source.isEmpty()) {
            for (Object obj : source) {
                Optional<T> t = deepCopyObject(obj, target);
                list.add(t.orElseThrow(() -> new UtilException("无法复制对象")));
            }
        }
        return list;
    }

    public interface PropertyValidator {
        boolean isValid(Object value);
    }

}
