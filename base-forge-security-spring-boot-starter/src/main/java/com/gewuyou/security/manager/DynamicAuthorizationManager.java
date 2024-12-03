package com.gewuyou.security.manager;

import com.gewuyou.security.service.IDynamicSecurityService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 动态授权管理器
 *
 * @author gewuyou
 * @since 2024-11-06 23:51:24
 */
@Slf4j
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private static Map<String, ConfigAttribute> configAttributeMap;
    private final IDynamicSecurityService dynamicSecurityService;
    private final AntPathMatcher antPathMatcher;
    public DynamicAuthorizationManager(IDynamicSecurityService dynamicSecurityService) {
        this.dynamicSecurityService = dynamicSecurityService;
        this.antPathMatcher = new AntPathMatcher();
    }

    /**
     * 加载数据
     */
    @PostConstruct
    public void refreshDataSources() {
        configAttributeMap = dynamicSecurityService.loadDataSource();
    }

    private boolean isOptionsRequest(HttpServletRequest request) {
        return HttpMethod.OPTIONS.matches(request.getMethod());
    }
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();
        // 判断是否是options请求
        if (isOptionsRequest(request)) {
            return new AuthorizationDecision(true);
        }
        log.info("检查的URL：{}", request.getRequestURI());
        // 获取当前用户的权限列表
        List<String> permissions = authentication
                .get()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        log.info("当前用户的权限列表：{}", permissions);
        // 获取当前请求的权限列表
        Collection<ConfigAttribute> attributes = getAttributes(request);
        // 遍历权限列表，判断是否有访问权限
        // 判断当前用户是否有当前请求的权限
        for (ConfigAttribute attribute : attributes) {
            log.info("当前请求的权限：{}, 请求的URL：{}", attribute.getAttribute(), request.getRequestURI());
            // 允许匿名访问
            if(hasAccess(permissions, attributes)){
                return new AuthorizationDecision(true);
            }
        }
        log.info("当前用户没有访问权限：{}", request.getRequestURI());
        // 没有权限
        return new AuthorizationDecision(false);
    }

    /**
     * 判断当前用户是否有访问权限
     * @param permissions    当前用户的权限列表
     * @param attributes     当前请求的权限列表
     * @return true：有访问权限；false：没有访问权限
     */
    private boolean hasAccess(List<String> permissions, Collection<ConfigAttribute> attributes) {
        for (ConfigAttribute attribute : attributes) {
            if ("anonymous".equals(attribute.getAttribute())) {
                return true;
            }
            if (permissions.contains(attribute.getAttribute())) {
                return true;
            }
        }
        return false;
    }
    /**
     * 根据传入的HttpServletRequest对象获取对应的安全配置属性
     * <p>
     * 如果configAttributeMap为空，则重新加载数据源。根据请求的URL和HTTP方法，
     * 使用AntPathMatcher进行模式匹配，返回匹配到的配置属性列表。
     *
     * @param request 请求对象
     * @return 返回匹配到的配置属性集合，如果没有匹配到则返回空集合
     * @throws IllegalArgumentException 如果传入的object不是HttpServletRequest实例
     */
    public Collection<ConfigAttribute> getAttributes(HttpServletRequest request) {
        if (CollectionUtils.isEmpty(configAttributeMap)) {
            this.refreshDataSources();
        }
        String method = request.getMethod();
        String url = request.getRequestURI();
        for (Map.Entry<String, ConfigAttribute> entry : configAttributeMap.entrySet()) {
            String[] key = entry.getKey().split(":");
            if (antPathMatcher.match(key[0], url) && method.equals(key[1])) {
                String[] roles = entry.getValue().getAttribute().split(",");
                return SecurityConfig.createList(roles);
            }
        }
        // 没有匹配到，返回空集合
        return SecurityConfig.createList();
    }

}
