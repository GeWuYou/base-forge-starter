package com.gewuyou.security.service.impl;

import com.gewuyou.security.exception.NotRealizedException;
import com.gewuyou.security.service.IUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 默认用户信息服务实现
 *
 * @author gewuyou
 * @since 2024-11-24 20:42:47
 */
public class DefaultUserDetailsService implements IUserDetailsService {

    /**
     * 根据signInId查询用户详细信息
     *
     * @param signInId signInId
     * @return UserDetails
     */
    @Override
    public UserDetails loadUserBySignInId(String signInId)  {
        throw new NotRealizedException("not realized");
    }
}
