package com.gewuyou.baseforge.redis.i18n.enums;


import com.gewuyou.baseforge.autoconfigure.i18n.entity.InternalInformation;

/**
 * 缓存响应信息
 *
 * @author gewuyou
 * @since 2024-11-27 11:40:57
 */
public enum CacheResponseInformation implements InternalInformation {

    /**
     * json序列化错误
     */
    JSON_SERIALIZE_ERROR("json.serialize.error"),
    JSON_DESERIALIZE_ERROR("json.deserialize.error"),
    ;
    private final String i18nMessageCode;

    CacheResponseInformation(String i18nMessageCode) {
        this.i18nMessageCode = i18nMessageCode;
    }

    @Override
    public String getResponseI8nMessageCode() {
        return i18nMessageCode;
    }
}
