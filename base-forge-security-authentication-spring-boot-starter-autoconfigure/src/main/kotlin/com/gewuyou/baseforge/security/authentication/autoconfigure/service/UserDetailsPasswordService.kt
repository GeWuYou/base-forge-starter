package com.gewuyou.baseforge.security.authentication.autoconfigure.service

import com.gewuyou.baseforge.security.authentication.entities.entity.UserDetails


/**
 *用户详细信息密码服务
 *
 * @since 2025-02-15 17:09:48
 * @author gewuyou
 */
fun interface UserDetailsPasswordService {
    /**
     * Modify the specified user's password. This should change the user's password in the
     * persistent user repository (database, LDAP etc).
     * @param user the user to modify the password for
     * @param newPassword the password to change to, encoded by the configured
     * `PasswordEncoder`
     * @return the updated UserDetails with the new password
     */
    fun updatePassword(user: UserDetails, newPassword: String): UserDetails
}