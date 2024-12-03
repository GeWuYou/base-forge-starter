package com.gewuyou.security.dto;

import com.gewuyou.security.entity.Principal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 普通登录请求DTO
 *
 * @author gewuyou
 * @since 2024-11-05 11:08:32
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NormalLoginReqDTO {
    /**
     * 用户登录传递的信息(用户名 邮箱 手机号 记住我)
     */
    private Principal principal;
    /**
     * 用户登录传递的凭证(密码 验证码)
     */
    private String credentials;
}
