package com.wingedtech.common.constant;

/**
 * 统一定义各服务间常用的权限
 */
public class CommonPermissions {
    /**
     * 系统权限名称的统一前缀
     */
    public static final String PERMISSION_PREFIX = "PERM_";
    /**
     * 调试接口权限 - 具有此权限的用户可以在网关后台上访问调试接口（开发用）
     */
    public static final String PERMISSION_DEBUG = PERMISSION_PREFIX + "DEBUG";
    /**
     * SWAGGER文档权限 - 具有此权限的用户可以在网关后台上访问swagger文档（开发用）
     */
    public static final String PERM_SWAGGER = PERMISSION_PREFIX + "SWAGGER";
    /**
     * 运维管理权限 - 具有此权限的用户访问各个服务的/management/运维接口（运维用）
     */
    public static final String PERM_DEVOPS_MANAGEMENT = PERMISSION_PREFIX + "DEVOPS_MANAGEMENT";

}
