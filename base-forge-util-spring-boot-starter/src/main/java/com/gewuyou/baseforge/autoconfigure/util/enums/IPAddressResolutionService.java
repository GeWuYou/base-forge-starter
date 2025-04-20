package com.gewuyou.baseforge.autoconfigure.util.enums;

import lombok.Getter;

import java.util.Map;

/**
 * ip解析服务地址枚举
 *
 * @author gewuyou
 * @since 2024-10-04 23:54:58
 */
@Getter
public enum IPAddressResolutionService {
    IP_API("http://ip-api.com/json/","zh-CN","status,message,country,city,query");

    private final String url;

    private final String language;

    private final String fields;

    IPAddressResolutionService(String url, String language, String fields) {
        this.url = url;
        this.language = language;
        this.fields = fields;
    }
    public static Map<String, Object> getParams(IPAddressResolutionService service) {
        return switch (service) {
            case IP_API -> Map.of(
                    "lang", service.language,
                    "fields", service.fields
            );
            default -> Map.of();
        };
    }
}
