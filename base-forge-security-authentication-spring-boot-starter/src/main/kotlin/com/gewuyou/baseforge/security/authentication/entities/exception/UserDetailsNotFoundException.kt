package com.gewuyou.baseforge.security.authentication.entities.exception

import com.gewuyou.baseforge.autoconfigure.i18n.entity.ResponseInformation

/**
 * 未找到用户详细信息异常
 *
 * @author gewuyou
 * @since 2024-12-31 11:12:25
 */
class UserDetailsNotFoundException(responseInformation: ResponseInformation) :
    AuthenticationException(responseInformation)
