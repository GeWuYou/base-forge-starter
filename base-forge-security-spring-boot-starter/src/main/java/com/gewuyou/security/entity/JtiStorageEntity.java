package com.gewuyou.security.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * JTI 存储实体
 *
 * @author gewuyou
 * @since 2024-11-30 23:36:03
 */
@Data
@Builder
public class JtiStorageEntity {
    /**
     * JWT ID
     */
    private String jti;
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
}
