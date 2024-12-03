package com.gewuyou.security.manager;

import com.gewuyou.security.entity.JtiStorageEntity;
import com.gewuyou.security.exception.AuthenticationException;
import com.gewuyou.security.i18n.enums.SecurityResponseInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存 JTI 管理器
 *
 * @author gewuyou
 * @since 2024-11-30 23:34:22
 */
@Slf4j
public class MemoryJtiManager implements JtiManager {
    /**
     * JWT ID 存储 Map
     */
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, JtiStorageEntity>> jtiStorageMap = new ConcurrentHashMap<>();
    /**
     * 判断 JWT ID 是否有效
     *
     * @param jti        JWT ID
     * @param userAuthId 用户认证 ID
     * @return true 有效，false 无效
     */
    @Override
    public boolean isJtiValid(String jti, String userAuthId) {
        ConcurrentHashMap<String, JtiStorageEntity> jtiMap = jtiStorageMap.get(userAuthId);
        if (CollectionUtils.isEmpty(jtiMap)) {
            return false;
        }
        synchronized (jtiMap) {
            JtiStorageEntity jtiStorageEntity = jtiMap.get(jti);
            // 如果不存在或者过期，则返回 false
            return !Objects.isNull(jtiStorageEntity) && !jtiStorageEntity.getExpireTime().isBefore(LocalDateTime.now());
        }
    }

    /**
     * 添加 JWT ID
     *
     * @param userAuthId 用户认证 ID
     * @param jti        JWT ID
     * @param expireTime 过期时间
     */
    @Override
    public void addJti(String userAuthId, String jti, long expireTime) {
        ConcurrentHashMap<String, JtiStorageEntity> jtiStorageEntityMap = jtiStorageMap.computeIfAbsent(userAuthId, v -> new ConcurrentHashMap<>());
        if (Objects.nonNull(jtiStorageEntityMap
                .putIfAbsent(jti,
                        JtiStorageEntity
                                .builder()
                                .jti(jti)
                                .expireTime(LocalDateTime.now().plus(expireTime, ChronoUnit.MILLIS))
                                .build()
                ))) {
            log.warn("用户认证id:{}, JWT ID 已存在!", userAuthId);
            throw new AuthenticationException(SecurityResponseInformation.DUPLICATE_SIGN_IN_REQUESTS);
        }
    }

    /**
     * 删除 JWT ID
     *
     * @param userAuthId 用户认证 ID
     * @param jti        JWT ID
     */
    @Override
    public void removeJti(String userAuthId, String jti) {
        jtiStorageMap.computeIfPresent(userAuthId, (key, jtiMap) -> {
            jtiMap.remove(jti);
            // 如果清空了所有 JTI，移除用户数据
            return jtiMap.isEmpty() ? null : jtiMap;
        });
    }

    /**
     * 更新 JWT ID
     *
     * @param userAuthId 用户认证 ID
     * @param jti        JWT ID
     * @param expireTime 过期时间
     */
    @Override
    public void updateJti(String userAuthId, String jti, long expireTime) {
        jtiStorageMap.computeIfPresent(userAuthId, (key, jtiMap) -> {
            JtiStorageEntity entity = jtiMap.get(jti);
            if (Objects.isNull(entity)) {
                log.warn("用户认证id:{}, JWT ID 不存在或jti已过期!", userAuthId);
                throw new AuthenticationException(SecurityResponseInformation.CERTIFICATION_HAS_EXPIRED);
            }
            // 替换为新的对象
            jtiMap.put(jti, JtiStorageEntity
                    .builder()
                    .jti(jti)
                    .expireTime(LocalDateTime.now().plus(expireTime, ChronoUnit.MILLIS))
                    .build());
            return jtiMap;
        });
    }

    /**
     * 清除 JWT ID
     *
     * @param userAuthId 用户认证 ID
     */
    @Override
    public void clearJti(String userAuthId) {
        jtiStorageMap.remove(userAuthId);
    }
}
