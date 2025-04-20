package com.gewuyou.baseforge.security.authentication.entities.entity.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * 默认登录请求
 *
 * @author gewuyou
 * @since 2025-02-17 00:12:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultLoginRequest implements LoginRequest {
    @JsonProperty("principal")
    private Object principal;
    @JsonProperty("credentials")
    private Object credentials;
    private String loginType;

    @NotNull
    @Override
    public String getLoginType() {
        return loginType;
    }
}
