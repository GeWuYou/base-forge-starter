package com.gewuyou.security.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.access.ConfigAttribute;

import java.util.List;

/**
 * 扩展的 config 属性
 *
 * @author gewuyou
 * @since 2024-11-24 14:48:59
 */
@AllArgsConstructor
public class ExtendedConfigAttribute implements ConfigAttribute {
    /**
     * 权限标识
     */
    private String attribute;
    /**
     * 是否匿名
     */
    @Getter
    private boolean isAnonymous;
    /**
     * 角色列表
     */
    @Getter
    private List<String> roles;
    @Override
    public String getAttribute() {
        return attribute;
    }
}
