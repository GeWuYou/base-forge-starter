package com.gewuyou.security.service;

import org.springframework.security.access.ConfigAttribute;

import java.util.Map;

/**
 * 动态安全服务接口
 *
 * @author gewuyou
 * @since 2024-11-07 22:07:20
 */
public interface IDynamicSecurityService {
    /**
     * 加载资源ANT通配符和资源对应MAP
     */
    Map<String, ConfigAttribute> loadDataSource();
}
