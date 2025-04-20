package com.gewuyou.baseforge.entities.web.i18n.enums;


import com.gewuyou.baseforge.autoconfigure.i18n.entity.ResponseInformation;
import org.springframework.http.HttpStatus;

/**
 * Web 响应信息
 *
 * @author gewuyou
 * @since 2024-11-26 17:24:47
 */
public enum WebResponseInformation implements ResponseInformation {
    /**
     * 内部错误
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "web.invalid.server.error"),
    /**
     * 过多请求
     */
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS.value(), "web.too.many.requests"),
    /**
     * 参数错误
     */
    ILLEGAL_PARAMETERS(HttpStatus.BAD_REQUEST.value(), "web.illegal.parameters"),
    /**
     * 重复请求
     */
    REPEAT_REQUEST(HttpStatus.BAD_REQUEST.value(), "web.repeat.request"),
    /**
     * 操作成功
     */
    OPERATION_SUCCESSFUL(HttpStatus.OK.value(), "web.operation.success"),
    /**
     * 设备ID未提供
     */
    DEVICE_ID_NOT_PROVIDED(HttpStatus.BAD_REQUEST.value(), "web.device.id.not.provided");

    private final int code;
    private final String i18nMessageCode;

    WebResponseInformation(int code, String i18nMessageCode) {
        this.code = code;
        this.i18nMessageCode = i18nMessageCode;
    }

    @Override
    public int getResponseCode() {
        return this.code;
    }

    @Override
    public String getResponseI8nMessageCode() {
        return this.i18nMessageCode;
    }
}
