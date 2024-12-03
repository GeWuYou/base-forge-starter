package com.gewuyou.security.manager;

import com.gewuyou.redis.service.ICacheService;
import com.gewuyou.security.exception.AuthenticationException;
import com.gewuyou.security.i18n.enums.SecurityResponseInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Set;

/**
 * JTI 缓存管理器
 *
 * @author gewuyou
 * @since 2024-12-02 11:04:21
 */
@Slf4j
public class CacheJtiManager implements JtiManager {
    private static final String JTI_KEY_PREFIX = "auth:jti:";
    /**
     * 用于存储用户的 JTI 列表
     */
    private static final String USER_JTI_LIST_KEY_PREFIX = "auth:user:jti:";
    private final ICacheService cacheService;

    public CacheJtiManager(ICacheService cacheService) {
        this.cacheService = cacheService;
    }
    /**
     * 判断 JWT ID 是否有效
     *
     * @param jti        JWT ID
     * @param userAuthId 用户认证 ID
     * @return true 有效，false 无效
     */
    @Override
    public boolean isJtiValid(String jti, String userAuthId) {
        String key = buildKey(userAuthId, jti);
        Object cached = cacheService.get(key);
        return Objects.nonNull(cached);
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
        String key = buildKey(userAuthId, jti);
        Object o = cacheService.get(key);
        if(Objects.nonNull(o)) {
            log.warn("用户认证id:{}, JWT ID 已存在!", userAuthId);
            throw new AuthenticationException(SecurityResponseInformation.DUPLICATE_SIGN_IN_REQUESTS);
        }
        cacheService.set(key,jti,expireTime);
        // 将 JTI 添加到用户的 JTI 列表
        String userJtiListKey = buildUserJtiListKey(userAuthId);
        // 将 JTI 添加到集合中
        cacheService.sAdd(userJtiListKey, jti);
    }

    /**
     * 删除 JWT ID
     *
     * @param userAuthId 用户认证 ID
     * @param jti        JWT ID
     */
    @Override
    public void removeJti(String userAuthId, String jti) {
        cacheService.delete(buildKey(userAuthId, jti));
        // 从用户的 JTI 列表中删除 JTI
        String userJtiListKey = buildUserJtiListKey(userAuthId);
        cacheService.sRemove(userJtiListKey, jti);
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
        String key = buildKey(userAuthId, jti);
        Object o = cacheService.get(key);
        if(Objects.isNull(o)) {
            log.warn("用户认证id:{}, JWT ID 不存在或jti已过期!", userAuthId);
            throw new AuthenticationException(SecurityResponseInformation.CERTIFICATION_HAS_EXPIRED);
        }
        cacheService.set(key,jti,expireTime);
        // 将 JTI 添加到用户的 JTI 列表
        String userJtiListKey = buildUserJtiListKey(userAuthId);
        // 将 JTI 添加到集合中
        cacheService.sAdd(userJtiListKey, jti);
    }

    /**
     * 清除 JWT ID
     *
     * @param userAuthId 用户认证 ID
     */
    @Override
    public void clearJti(String userAuthId) {
        String userJtiListKey = buildUserJtiListKey(userAuthId);
        Set<Object> sMembers = cacheService.sMembers(userJtiListKey);
        for (Object sMember : sMembers) {
            String key = sMember.toString();
            cacheService.delete(key);
        }
        cacheService.sRemove(userJtiListKey);
    }

    /**
     * 构造缓存键
     *
     * @param userAuthId 用户认证 ID
     * @param jti        JWT ID
     * @return 缓存键
     */
    private String buildKey(String userAuthId, String jti) {
        if (!StringUtils.hasText(userAuthId) || !StringUtils.hasText(jti)) {
            throw new IllegalArgumentException("UserAuthId 或 JTI 不能为空");
        }
        return JTI_KEY_PREFIX + userAuthId + ":" + jti;
    }
    /**
     * 构造用户的 JTI 列表键
     *
     * @param userAuthId 用户认证 ID
     * @return 用户的 JTI 列表缓存键
     */
    private String buildUserJtiListKey(String userAuthId) {
        if (!StringUtils.hasText(userAuthId)) {
            throw new IllegalArgumentException("UserAuthId 不能为空");
        }
        return USER_JTI_LIST_KEY_PREFIX + userAuthId;
    }
}
