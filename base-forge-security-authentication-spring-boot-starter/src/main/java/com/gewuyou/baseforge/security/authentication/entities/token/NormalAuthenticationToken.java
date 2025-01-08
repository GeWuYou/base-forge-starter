package com.gewuyou.baseforge.security.authentication.entities.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Objects;

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
public class NormalAuthenticationToken extends AbstractAuthenticationToken {
    /**
     * 用户唯一标识(通常在登录前为用户名 邮箱或手机号)
     */
    private transient Object principal;
    /**
     * 用户登录传递的凭证(密码)
     */
    private transient Object credentials;

    public NormalAuthenticationToken(UserDetails userDetails, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        setDetails(userDetails);
        super.setAuthenticated(true);
    }

    /**
     * 创建未认证的令牌
     *
     * @param principal   登录唯一标识(通常在登录前为用户名 邮箱或手机号)
     * @param credentials 登录凭证
     * @return 未认证的令牌
     */
    public static NormalAuthenticationToken unauthenticated(Object principal, Object credentials) {
        return new NormalAuthenticationToken(principal, credentials);
    }


    public NormalAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.setAuthenticated(false);
    }

    public NormalAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    /**
     * 创建已认证的令牌
     *
     * @param principal   用户唯一标识(通常在登录前为用户名 邮箱或手机号)
     * @param credentials 登录凭证
     * @param authorities 权限
     * @return 已认证的令牌
     */
    public static NormalAuthenticationToken authenticated(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        return new NormalAuthenticationToken(principal, credentials, authorities);
    }

    /**
     * 创建已认证的令牌
     *
     * @param userDetails 登录信息
     * @param authorities 权限
     * @return 已认证的令牌
     */
    public static NormalAuthenticationToken authenticated(UserDetails userDetails,Collection<? extends GrantedAuthority> authorities) {
        return new NormalAuthenticationToken(userDetails, authorities);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NormalAuthenticationToken that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(principal, that.principal) && Objects.equals(credentials, that.credentials);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), principal, credentials);
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
