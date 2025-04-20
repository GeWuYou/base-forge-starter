package com.gewuyou.baseforge.security.authentication.entities.exception

import com.gewuyou.baseforge.autoconfigure.i18n.entity.ResponseInformation
import org.springframework.security.core.AuthenticationException as BaseAuthenticationException


/**
 * 认证异常
 *
 * @since 2024-11-27 23:59:29
 * @author gewuyou
 */
open class AuthenticationException(
    responseInformation: ResponseInformation
) : BaseAuthenticationException(
    responseInformation.responseI8nMessageCode
)
