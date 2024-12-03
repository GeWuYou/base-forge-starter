package com.gewuyou.security.service;

import com.gewuyou.security.exception.SignInIdNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 用户详细信息服务接口
 *
 * @author gewuyou
 * @since 2024-11-05 19:34:50
 */
public interface IUserDetailsService {
    /**
     * 根据signInId查询用户详细信息
     * @param signInId signInId
     * @return UserDetails
     * @throws SignInIdNotFoundException signInId不存在异常
     */
    UserDetails loadUserBySignInId(String signInId) throws SignInIdNotFoundException;
}
