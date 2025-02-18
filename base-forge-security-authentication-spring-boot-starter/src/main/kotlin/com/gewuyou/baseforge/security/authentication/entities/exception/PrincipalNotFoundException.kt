package com.gewuyou.baseforge.security.authentication.entities.exception

import com.gewuyou.baseforge.autoconfigure.i18n.entity.ResponseInformation

/**
 * 用户身份标识 未找到异常
 *
 * @author gewuyou
 * @since 2024-12-31 00:04:22
 */
class PrincipalNotFoundException(responseInformation: ResponseInformation) :
    AuthenticationException(responseInformation)
