package com.gewuyou.baseforge.security.authorization.autoconfigure.config.entity

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 安全授权属性
 *
 * @author gewuyou
 * @since 2025-01-08 14:42:33
 */
@ConfigurationProperties("base-forge.security.authorization")
data class SecurityAuthorizationProperties(
    /**
     * 排除的URL
     */
    var ignoredUrls: Array<String?> = arrayOfNulls(0),

    /**
     * 请求URL(默认鉴权请求URL：/api)
     */
    var requestUrl: String = "/api"
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SecurityAuthorizationProperties) return false
        if (!ignoredUrls.contentEquals(other.ignoredUrls)) return false
        if (requestUrl != other.requestUrl) return false
        return true
    }

    override fun hashCode(): Int {
        var result = ignoredUrls.contentHashCode()
        result = 31 * result + requestUrl.hashCode()
        return result
    }
}