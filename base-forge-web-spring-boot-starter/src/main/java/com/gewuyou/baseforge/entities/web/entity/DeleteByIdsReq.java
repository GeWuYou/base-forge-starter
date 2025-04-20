package com.gewuyou.baseforge.entities.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 根据id列表删除请求
 *
 * @author gewuyou
 * @since 2025-01-18 17:39:18
 */
@Schema(description = "根据id列表删除请求")
@JsonIgnoreProperties(ignoreUnknown = true)
public record DeleteByIdsReq(@NotNull(message = "web.delete.ids.notNull") List<Long> ids) {
}
