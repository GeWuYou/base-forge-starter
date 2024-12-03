package com.gewuyou.security.service.impl;

import com.gewuyou.security.exception.NotRealizedException;
import com.gewuyou.security.service.IDynamicSecurityService;
import org.springframework.security.access.ConfigAttribute;

import java.util.Map;

/**
 * 默认动态安全服务
 *
 * @author gewuyou
 * @since 2024-11-24 22:19:00
 */
public class DefaultDynamicSecurityService implements IDynamicSecurityService {
    /**
     * 加载资源ANT通配符和资源对应MAP
     */
    @Override
    public Map<String, ConfigAttribute> loadDataSource() {
        throw new NotRealizedException("not realized");
    }
}
