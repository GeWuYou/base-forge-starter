package com.gewuyou.web.entity;


import com.gewuyou.i18n.config.I18nAutoConfiguration;
import com.gewuyou.i18n.enums.ResponseInformation;
import com.gewuyou.util.SpringUtil;
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
    private MessageSource i18nMessageSource;
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
    private Result() {
        this.i18nMessageSource = (MessageSource) SpringUtil.getBean(I18nAutoConfiguration.MESSAGE_SOURCE_BEAN_NAME);
    }

    private Result(ResponseInformation responseInformation, boolean success, T data, MessageSource messageSource, Object... args) {
        this.code = responseInformation.getCode();
        this.success = success;
        this.message = messageSource.getMessage(responseInformation.getMessage(), args, LocaleContextHolder.getLocale());
        this.data = data;
    }

    private Result(ResponseInformation responseInformation, boolean success, T data, Object... args) {
        this.code = responseInformation.getCode();
        this.success = success;
        this.message = i18nMessageSource.getMessage(responseInformation.getMessage(), args, LocaleContextHolder.getLocale());
        this.data = data;
    }

    private Result(ResponseInformation responseInformation, boolean success, T data, MessageSource messageSource) {
        this.code = responseInformation.getCode();
        this.success = success;
        this.message = messageSource.getMessage(responseInformation.getMessage(), null, LocaleContextHolder.getLocale());
        this.data = data;
    }

    private Result(ResponseInformation responseInformation, boolean success, T data) {
        this.code = responseInformation.getCode();
        this.success = success;
        this.message = i18nMessageSource.getMessage(responseInformation.getMessage(), null, LocaleContextHolder.getLocale());
        this.data = data;
    }


    private Result(Integer code, String message, MessageSource messageSource, Object... args) {
        this.code = code;
        this.message = messageSource.getMessage(message, args, LocaleContextHolder.getLocale());
        this.success = false;
    }

    private Result(Integer code, String message, Object... args) {
        this.code = code;
        this.message = i18nMessageSource.getMessage(message, args, LocaleContextHolder.getLocale());
        this.success = false;
    }

    private Result(Integer code, String message, MessageSource messageSource) {
        this.code = code;
        this.message = messageSource.getMessage(message, null, LocaleContextHolder.getLocale());
        this.success = false;
    }

    private Result(Integer code, String message) {
        this.code = code;
        this.message = i18nMessageSource.getMessage(message, null, LocaleContextHolder.getLocale());
        this.success = false;
    }

    private Result(ResponseInformation responseInformation, MessageSource messageSource, Object... args) {
        this.code = responseInformation.getCode();
        this.message = messageSource.getMessage(message, args, LocaleContextHolder.getLocale());
        this.success = false;
    }

    private Result(ResponseInformation responseInformation, Object... args) {
        this.code = responseInformation.getCode();
        this.message = i18nMessageSource.getMessage(message, args, LocaleContextHolder.getLocale());
        this.success = false;
    }

    /**
     * 无数据成功返回
     *
     * @return 成功结果
     */
    public static <T> Result<T> success(MessageSource messageSource) {
        return new Result<>(ResponseInformation.OPERATION_SUCCESSFUL, true, null, messageSource);
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
        return new Result<>(ResponseInformation.OPERATION_SUCCESSFUL, true, data, messageSource);
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
    public static Result<String> failure(int code, String message) {
        return new Result<>(code, message);
    }

    /**
     * 失败返回
     *
     * @return 失败结果
     */
    public static Result<String> failure(String message, MessageSource messageSource) {
        return new Result<>(HttpStatus.BAD_REQUEST.value(), message, messageSource);
    }

    /**
     * 失败返回
     *
     * @return 失败结果
     */
    public static Result<String> failure(int code, String message, MessageSource messageSource) {
        return new Result<>(code, message, messageSource);
    }

    /**
     * 失败返回
     *
     * @return 失败结果
     */
    public static Result<String> failure(ResponseInformation responseInformation, MessageSource messageSource, Object... args) {
        return new Result<>(responseInformation, messageSource, args);
    }


    /**
     * 设置响应信息失败返回
     *
     * @param responseInformation 响应代码枚举
     * @return 失败结果
     */
    public static Result<String> failure(ResponseInformation responseInformation, MessageSource messageSource) {
        return new Result<>(responseInformation, false, null, messageSource);
    }
}



