package com.gewuyou.security.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * 普通登录请求令牌</br>
 * SpringSecurity传输登录认证的数据的载体，相当一个Dto</br>
 * 必须是 {@link Authentication} 实现类</br>
 * 这里选择extends{@link AbstractAuthenticationToken}，而不是直接implements Authentication,</br>
 * 是为了少些写代码。因为{@link Authentication}定义了很多接口，我们用不上。</br>
 * 这里只需要实现 {@link Authentication#getCredentials()} 和 {@link Authentication#getPrincipal()} 即可。
 *
 * @author gewuyou
 * @since 2024-11-04 00:53:37
 */
public class NormalLoginAuthenticationToken extends AbstractAuthenticationToken {
    /**
     * 用户登录传递的信息(通常在登录前为用户名 邮箱 手机号 记住我,在登录后为需要返回的登录信息)
     */
    private final Object principal;
    /**
     * 用户登录传递的凭证(密码)
     */
    private Object credentials;


    public NormalLoginAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.setAuthenticated(false);
    }

    public NormalLoginAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    /**
     * 创建未认证的令牌
     *
     * @param principal   登录信息
     * @param credentials 登录凭证
     * @return 未认证的令牌
     */
    public static NormalLoginAuthenticationToken unauthenticated(Object principal, Object credentials) {
        return new NormalLoginAuthenticationToken(principal, credentials);
    }

    /**
     * 创建已认证的令牌
     *
     * @param principal   登录信息
     * @param credentials 登录凭证
     * @param authorities 权限
     * @return 已认证的令牌
     */
    public static NormalLoginAuthenticationToken authenticated(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        return new NormalLoginAuthenticationToken(principal, credentials, authorities);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    /**
     * 在认证成功后调用，清除凭证信息
     */
    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        Assert.isTrue(!isAuthenticated, "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }
}
