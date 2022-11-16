package com.wingedtech.common.constant;

/**
 * @author sunbjx
 * @since 2019/1/21 2019
 */
public interface Requests {

    String ANT_PATTERN = "/**";
    /**
     * 所有公开访问接口的根路径（任何用户可访问）
     */
    String API_PUBLIC = "/api/public";
    /**
     * 所有已登录用户可访问的根路径
     */
    String API_USER = "/api/user";
    /**
     * 所有debug接口的根路径（PERM_DEBUG权限及ROLE_ADMIN可访问）
     */
    String API_DEBUG = "/api/debug";
    /**
     * 服务内部接口的根路径（PERM_INTERNAL权限及ROLE_ADMIN可访问）
     */
    String API_INTERNAL = "/api/internal";
    /**
     * 运维接口的根路径（PERM_DEVOPS_MANAGEMENT权限及ROLE_ADMIN可访问）
     */
    String API_DEVOPS = "/api/devops";
    /**
     * ant pattern - 所有公开访问接口
     */
    String API_PUBLIC_ANT_PATTERN = API_PUBLIC + ANT_PATTERN;
    /**
     * ant pattern - 所有debug接口
     */
    String API_DEBUG_ANT_PATTERN = API_DEBUG + ANT_PATTERN;
}
