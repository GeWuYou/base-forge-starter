package com.gewuyou.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gewuyou.util.exception.UtilException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
                t.ifPresent(list::add);
                t.orElseThrow(
                        () -> new UtilException("无法复制对象"));
            }
        }
        return list;
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
                t.ifPresent(list::add);
                t.orElseThrow(() -> new UtilException("无法复制对象"));
            }
        }
        return list;
    }
}
