package com.gewuyou.baseforge.security.authentication.autoconfigure.service;


import com.gewuyou.baseforge.security.authentication.entities.exception.SignInIdNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * 用户详细信息服务接口
 *
 * @author gewuyou
 * @since 2024-11-05 19:34:50
 */
public interface UserDetailsService {
    /**
     * 根据用户唯一标识查询用户详细信息
     * @param principal 用户唯一标识 通常为用户名 手机号 邮箱等
     * @return UserDetails 用户详细信息
     * @throws SignInIdNotFoundException signInId不存在异常
     */
    Optional<? extends UserDetails> loadUserBySignInId(Object principal) throws SignInIdNotFoundException;

    /**
     * 根据用户唯一标识查询用户唯一标识
     * @param principal 用户唯一标识 通常为用户名 手机号 邮箱等
     * @apiNote 用于支持多种唯一标识登录时，根据登录时用户唯一标识查询用户确定的唯一标识
     * @return 用户唯一标识
     */
    Optional<String> getUserPrincipal(Object principal);
}
