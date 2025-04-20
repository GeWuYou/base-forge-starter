package com.gewuyou.baseforge.entities.web.entity;


import com.gewuyou.baseforge.autoconfigure.i18n.entity.ResponseInformation;
import com.gewuyou.baseforge.entities.web.i18n.enums.WebResponseInformation;
import com.gewuyou.baseforge.trace.util.RequestIdUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * 返回结果包装类
 *
 * @author gewuyou
 * @since 2024-04-13 上午12:24:04
 */
@Schema(description = "返回结果包装类")
@Data
public class Result<T> implements Serializable {
    /**
     * 结果代码
     */
    @Schema(description = "结果代码", example = "200")
    private int code;
    /**
     * 是否成功
     */
    @Schema(description = "是否成功")
    private boolean success;
    /**
     * 响应信息
     */
    @Schema(description = "响应信息")
    private String message;
    /**
     * 结果数据
     */
    @Schema(description = "结果数据")
    private transient T data;
    /**
     * 请求ID，用于标识每个请求
     */
    @Schema(description = "请求ID")
    private String requestId;

    private Result(ResponseInformation responseInformation, boolean success, T data, MessageSource messageSource, Object... args) {
        this.code = responseInformation.getResponseCode();
        this.success = success;
        this.message = messageSource.getMessage(responseInformation.getResponseI8nMessageCode(), args, LocaleContextHolder.getLocale());
        this.data = data;
        this.requestId = RequestIdUtil.getRequestId();
    }


    private Result(ResponseInformation responseInformation, boolean success, T data, MessageSource messageSource) {
        this.code = responseInformation.getResponseCode();
        this.success = success;
        this.message = messageSource.getMessage(responseInformation.getResponseI8nMessageCode(), null, LocaleContextHolder.getLocale());
        this.data = data;
        this.requestId = RequestIdUtil.getRequestId();
    }

    private Result(Integer code, String i18nMessageCode, MessageSource messageSource, Object... args) {
        this.code = code;
        this.message = messageSource.getMessage(i18nMessageCode, args, LocaleContextHolder.getLocale());
        this.success = false;
        this.requestId = RequestIdUtil.getRequestId();
    }

    private Result(Integer code, String i18nMessageCode, MessageSource messageSource) {
        this.code = code;
        this.message = messageSource.getMessage(i18nMessageCode, null, LocaleContextHolder.getLocale());
        this.success = false;
        this.requestId = RequestIdUtil.getRequestId();
    }

    private Result(ResponseInformation responseInformation, MessageSource messageSource, Object... args) {
        this.code = responseInformation.getResponseCode();
        this.message = messageSource.getMessage(responseInformation.getResponseI8nMessageCode(), args, LocaleContextHolder.getLocale());
        this.success = false;
        this.requestId = RequestIdUtil.getRequestId();
    }

    /**
     * 无数据成功返回
     *
     * @return 成功结果
     */
    public static <T> Result<T> success(MessageSource messageSource) {
        return new Result<>(WebResponseInformation.OPERATION_SUCCESSFUL, true, null, messageSource);
    }


    /**
     * 设置响应信息成功返回
     *
     * @param responseInformation 响应代码枚举
     * @param <T>                 泛型
     * @return 成功结果
     */
    public static <T> Result<T> success(ResponseInformation responseInformation, MessageSource messageSource) {
        return new Result<>(responseInformation, true, null, messageSource);
    }


    /**
     * 有数据成功返回
     *
     * @param data 数据
     * @param <T>  泛型
     * @return 成功结果
     */

    public static <T> Result<T> success(T data, MessageSource messageSource) {
        return new Result<>(WebResponseInformation.OPERATION_SUCCESSFUL, true, data, messageSource);
    }

    /**
     * 有数据设置响应信息成功返回
     *
     * @param responseInformation 响应代码枚举
     * @param data                数据
     * @param <T>                 泛型
     * @return 成功结果
     */
    public static <T> Result<T> success(ResponseInformation responseInformation, T data, MessageSource messageSource) {
        return new Result<>(responseInformation, true, data, messageSource);
    }


    /**
     * 失败返回
     *
     * @return 失败结果
     */
    public static <T> Result<T> failure(String i18nMessageCode, MessageSource messageSource) {
        return new Result<>(HttpStatus.BAD_REQUEST.value(), i18nMessageCode, messageSource);
    }


    /**
     * 失败返回
     *
     * @return 失败结果
     */
    public static <T> Result<T> failure(int code, String i18nMessageCode, MessageSource messageSource) {
        return new Result<>(code, i18nMessageCode, messageSource);
    }

    /**
     * 失败返回
     *
     * @return 失败结果
     */
    public static <T> Result<T> failure(ResponseInformation responseInformation, MessageSource messageSource, Object... args) {
        return new Result<>(responseInformation, messageSource, args);
    }


    /**
     * 设置响应信息失败返回
     *
     * @param responseInformation 响应代码枚举
     * @param messageSource       消息源
     * @return 失败结果
     */
    public static <T> Result<T> failure(ResponseInformation responseInformation, MessageSource messageSource) {
        return new Result<>(responseInformation, false, null, messageSource);
    }
}



