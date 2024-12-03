package com.gewuyou.web.i18n.enums;

import com.gewuyou.i18n.entity.ResponseInformation;
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
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "invalid.server.error"),
    /**
     * 过多请求
     */
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS.value(), "too.many.requests"),
    /**
     * 参数错误
     */
    ILLEGAL_PARAMETERS(HttpStatus.BAD_REQUEST.value(), "illegal.parameters"),
    /**
     * 重复请求
     */
    REPEAT_REQUEST(HttpStatus.BAD_REQUEST.value(), "repeat.request"),
    /**
     * 操作成功
     */
    OPERATION_SUCCESSFUL(HttpStatus.OK.value(), "operation.success"),
    ;

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
