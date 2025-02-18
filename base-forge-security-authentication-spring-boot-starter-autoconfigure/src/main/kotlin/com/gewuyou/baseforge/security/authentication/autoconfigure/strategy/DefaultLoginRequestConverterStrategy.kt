package com.gewuyou.baseforge.security.authentication.autoconfigure.strategy

import com.gewuyou.baseforge.security.authentication.entities.entity.request.DefaultLoginRequest
import com.gewuyou.baseforge.security.authentication.entities.entity.request.LoginRequest
import com.gewuyou.baseforge.security.authentication.entities.token.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication


/**
 *默认登录请求转换器策略
 *
 * @since 2025-02-15 03:25:14
 * @author gewuyou
 */

/**
 * 用户名 密码 认证 Token 转换器策略
 */
class UsernamePasswordLoginRequestConverterStrategy : LoginRequestConverterStrategy {
    /**
     * 转换登录请求为认证对象
     * @param loginRequest 登录请求
     * @return 认证对象
     */
    override fun convert(loginRequest: LoginRequest): Authentication {
        if(loginRequest is DefaultLoginRequest){
            return UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.principal, loginRequest.credentials)
        }
        throw IllegalArgumentException("Unsupported login request type: ${loginRequest.javaClass.name}")
    }

    /**
     * 获取支持的登录类型
     * @return 请求的登录类型
     */
    override fun getSupportedLoginType(): String {
        return "username"
    }
}
