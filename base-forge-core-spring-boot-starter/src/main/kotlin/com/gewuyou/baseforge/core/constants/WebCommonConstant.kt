package com.gewuyou.baseforge.core.constants

/**
 * Web 公共常量
 *
 * @author gewuyou
 * @since 2025-01-02 09:31:21
 */
object WebCommonConstant {
    /**
     * 设备 ID 请求头
     */
    const val DEVICE_ID_HEADER: String = "X-Device-Id"

    /**
     * 设备 ID 请求属性
     */
    const val DEVICE_ID_ATTRIBUTE: String = "deviceId"

    /**
     * 请求 ID 请求头
     */
    const val REQUEST_ID_HEADER: String = "X-Request-Id"

    /**
     * 请求 ID 请求属性
     */
    const val REQUEST_ID_ATTRIBUTE: String = "requestId"

    /**
     * 请求 ID MDC 键
     */
    const val REQUEST_ID_MDC_KEY: String = "requestId"
}
