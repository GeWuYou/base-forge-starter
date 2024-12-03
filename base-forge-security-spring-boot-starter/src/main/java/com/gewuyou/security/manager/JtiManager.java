package com.gewuyou.security.manager;

/**
 * JWT ID 管理器
 *
 * @author gewuyou
 * @since 2024-11-30 14:05:50
 */
public interface JtiManager {
    /**
     * 判断 JWT ID 是否有效
     * @param jti JWT ID
     * @param userAuthId 用户认证 ID
     * @return true 有效，false 无效
     */
    boolean isJtiValid(String userAuthId, String jti);

    /**
     * 添加 JWT ID
     * @param userAuthId 用户认证 ID
     * @param jti JWT ID
     * @param expireTime 过期时间
     */
    void addJti(String userAuthId,String jti, long expireTime);

    /**
     * 删除 JWT ID
     * @param userAuthId 用户认证 ID
     * @param jti JWT ID
     */
    void removeJti(String userAuthId,String jti);

    /**
     * 更新 JWT ID
     * @param userAuthId 用户认证 ID
     * @param jti JWT ID
     * @param expireTime 过期时间
     */
    void updateJti(String userAuthId,String jti,long expireTime);

    /**
     * 清除 JWT ID
     * @param userAuthId 用户认证 ID
     */
    void clearJti(String userAuthId);
}
