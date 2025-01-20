package com.gewuyou.baseforge.autoconfigure.web.aspect;


import com.gewuyou.baseforge.autoconfigure.util.IpUtil;
import com.gewuyou.baseforge.entities.web.annotation.Idempotent;
import com.gewuyou.baseforge.entities.web.exception.AccessException;
import com.gewuyou.baseforge.entities.web.i18n.enums.WebResponseInformation;
import com.gewuyou.baseforge.redis.service.CacheService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 幂等切面
 *
 * @author gewuyou
 * @since 2024-07-20 上午12:00:39
 */
@Aspect
@Component
@Slf4j
public class IdempotentAspect {
    private final CacheService cacheService;

    @Autowired
    public IdempotentAspect(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        // 从线程上下文中获取 HttpServletRequest
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(requestAttributes)) {
            log.debug("非 Web 请求，不进行幂等处理");
            return joinPoint.proceed();
        }
        HttpServletRequest request = requestAttributes.getRequest();
        String key = getIdempotentKey(joinPoint, request);

        // 尝试在 Redis 中设置唯一键，设置成功表示首次请求，否则是重复请求
        Boolean isFirstRequest = cacheService.setIfAbsent(key, true, Duration.ofSeconds(idempotent.delay()));

        if (Boolean.FALSE.equals(isFirstRequest)) {
            // 如果键已存在，表示重复请求
            throw new AccessException(WebResponseInformation.REPEAT_REQUEST);
        }

        try {
            return joinPoint.proceed();
        } finally {
            // 请求处理完成后，延迟 5 秒后清除 Redis 中的键
            cacheService.delayedDelete(key, idempotent.delay(), TimeUnit.SECONDS);
        }
    }

    private String getIdempotentKey(ProceedingJoinPoint joinPoint, HttpServletRequest request) {
        // 根据请求参数生成唯一键，可以根据实际情况调整
        // 使用 ip地址、uri方法名和参数的哈希值作为键
        var ip = IpUtil.getIpAddress(request);
        log.info("ip: {}", ip);
        var methodName = joinPoint.getSignature().getName();
        var params = Arrays.toString(joinPoint.getArgs());
        return "idempotent:" + ip + ":" + methodName + ":" + params.hashCode();
    }
}
