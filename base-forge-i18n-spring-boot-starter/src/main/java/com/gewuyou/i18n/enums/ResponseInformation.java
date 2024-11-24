package com.gewuyou.i18n.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 响应信息
 *
 * @author gewuyou
 * @since 2024-10-03 10:41:45
 */
@Getter
public enum ResponseInformation {
    OPERATION_SUCCESSFUL(HttpStatus.OK.value(), "operation.success"),
    JSON_SERIALIZE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "json.serialize.error"),
    JSON_DESERIALIZE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "json.deserialize.error"),
    JSON_SERIALIZE_OR_DESERIALIZE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "json.serialize_or_deserialize.error"),
    ERROR_GET_LOCAL_IP_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "error.get_local_ip_failed"),
    URL_ENCODE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "url.encode.error"),
    PUBLIC_KEY_LOCATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "public_key.location.error"),
    LOG_BUILD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "log.build.failed"),
    USERNAME_EMPTY(HttpStatus.BAD_REQUEST.value(), "username.empty"),
    PASSWORD_EMPTY(HttpStatus.BAD_REQUEST.value(), "password.empty"),
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST.value(), "json.parse.error"),
    REPEAT_REQUEST(HttpStatus.BAD_REQUEST.value(), "repeat.request"),
    NOT_SUPPORTED_OPERATION(HttpStatus.NOT_IMPLEMENTED.value(), "not_supported.operation"),
    NOT_SUPPORTED_OAUTH2_LOGIN_TYPE(HttpStatus.NOT_IMPLEMENTED.value(), "not_supported.oauth2_login_type"),
    AUTH_PROVIDER_NOT_SUPPORTED(HttpStatus.NOT_IMPLEMENTED.value(), "auth_provider.not_supported"),
    SEND_EMAIL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "send_email.failed"),
    ADD_ATTACHMENT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "add.attachment.failed"),
    MAIL_SEND_SUCCESS(HttpStatus.OK.value(), "mail.send.success"),
    NOT_FOUND_JOB_CLASS(HttpStatus.NOT_FOUND.value(), "not_found.job_class"),
    REFRESH_CONFIG_FAILED(HttpStatus.BAD_REQUEST.value(), "refresh.config.failed"),
    CANNOT_BE_SCHEDULED_TASK(HttpStatus.BAD_REQUEST.value(), "cannot.be_scheduled_task"),
    OBJECT_COPY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "object.copy.failed"),
    CREATE_JOB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "create.job.error"),
    DELETE_TASK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "delete.task.failed"),
    RUN_TASK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "run.task.failed"),
    ASYNC_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR.value(), "async.exception"),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS.value(), "too.many.requests"),
    LOGIN_SUCCESS(HttpStatus.OK.value(), "login.success"),
    INNER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "inner.error"),
    COPY_PROPERTIES_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "copy.properties.failed");

    /**
     * 代码
     */
    private final int code;
    /**
     * 信息
     */
    private final String message;

    ResponseInformation(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
