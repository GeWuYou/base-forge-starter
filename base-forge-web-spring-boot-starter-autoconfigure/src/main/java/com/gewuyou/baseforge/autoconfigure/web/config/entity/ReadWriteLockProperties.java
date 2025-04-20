package com.gewuyou.baseforge.autoconfigure.web.config.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 读写锁属性
 *
 * @author gewuyou
 * @since 2025-01-23 15:02:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "base-forge.web.read-write-lock")
public class ReadWriteLockProperties {
    /**
     * 是否启用读写锁
     */
    private Boolean enabled = false;
}
