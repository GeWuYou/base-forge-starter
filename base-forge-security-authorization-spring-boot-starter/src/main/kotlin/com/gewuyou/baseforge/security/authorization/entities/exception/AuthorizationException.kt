package com.gewuyou.baseforge.security.authorization.entities.exception

import com.gewuyou.baseforge.autoconfigure.i18n.entity.ResponseInformation
import org.springframework.security.access.AccessDeniedException as BaseAccessDeniedException

/**
 * 授权异常
 *
 * @author gewuyou
 * @since 2025-01-06 15:30:26
 */
class AuthorizationException(responseInformation: ResponseInformation) :
    BaseAccessDeniedException(responseInformation.responseI8nMessageCode)
