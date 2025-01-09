package com.gewuyou.baseforge.autoconfigure.i18n.entity;

/**
 * 响应信息(用于对外提供i18n响应信息)
 *
 * @author gewuyou
 * @since 2024-11-26 15:43:06
 */
public interface ResponseInformation {
    /**
     * 获取响应码
     * @return 响应码
     */
    int getResponseCode();

    /**
     * 获取i18n响应信息code
     * @return 响应信息 code
     */
    String getResponseI8nMessageCode();
}
