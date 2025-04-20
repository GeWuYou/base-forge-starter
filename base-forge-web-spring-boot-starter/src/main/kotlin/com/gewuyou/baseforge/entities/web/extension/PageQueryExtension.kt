package com.gewuyou.baseforge.entities.web.extension

import com.gewuyou.baseforge.entities.web.entity.PageQuery
import com.gewuyou.baseforge.entities.web.enums.SortDirection
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort


/**
 *分页查询扩展
 *
 * @since 2025-01-17 15:29:27
 * @author gewuyou
 */

fun <T> PageQuery<T>.toPageable(defaultSort :Sort.Order=Sort.Order.desc("createdAt")): Pageable {
    val sortConditions = this.sortConditions
    // 检查排序条件
    if (sortConditions.isNotEmpty()) {
        val orders = sortConditions.stream().map {
            if (it.direction == SortDirection.ASC) Sort.Order.asc(it.field) else Sort.Order.desc(it.field)
        }.toList()
        return PageRequest.of(this.page - 1, this.pageSize, Sort.by(orders))
    }
    // 检查是否有单字段排序
    if (this.sortBy != null && this.sortDirection != null) {
        return PageRequest.of(
            this.page - 1,
            this.pageSize,
            Sort.by(if (this.sortDirection == SortDirection.ASC) Sort.Order.asc(this.sortBy) else Sort.Order.desc(this.sortBy))
        )
    }
    // 如果都没有则默认按创建时间排序
    return PageRequest.of(this.page - 1, this.pageSize, Sort.by(defaultSort))
}

/**
 * 获取默认的查询条件
 * @param builder CriteriaBuilder 标准生成器
 * @param root Root<Any> 查询的根对象
 * @param createAtName String 创建时间字段名
 * @param isEnabledName String 是否启用字段名
 * @param isDeletedName String 是否删除字段名
 * @return MutableList<Predicate> 查询条件列表
 */
fun <T,S> PageQuery<T>.getPredicates(
    builder: CriteriaBuilder,
    root: Root<S>,
    createAtName: String = "createdAt",
    isEnabledName: String = "isEnabled",
    isDeletedName: String = "isDeleted"
): MutableList<Predicate> {
    val predicates = mutableListOf<Predicate>()
    // 添加开始日期条件
    if (this.startDate != null) {
        predicates.add(builder.greaterThanOrEqualTo(root[createAtName], this.startDate))
    }
    // 添加结束日期条件
    if (this.endDate != null) {
        predicates.add(builder.lessThanOrEqualTo(root[createAtName], this.endDate))
    }
    // 添加是否启用条件
    if (this.isEnabled != null) {
        predicates.add(builder.equal(root.run { get<Boolean>(isEnabledName) }, this.isEnabled))
    }
    // 添加是否删除条件
    if (this.isDeleted != null) {
        predicates.add(builder.equal(root.run { get<Boolean>(isDeletedName) }, this.isDeleted))
    }
    return predicates
}