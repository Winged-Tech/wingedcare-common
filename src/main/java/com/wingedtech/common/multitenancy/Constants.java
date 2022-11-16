package com.wingedtech.common.multitenancy;

import com.wingedtech.common.constant.ConfigConstants;

/**
 * Constants.
 * @author taozhou
 */
public class Constants {
    public static final String CONFIG_MULTITENANCY_ROOT = ConfigConstants.WINGED_CONFIG_ROOT + "multitenancy";
    public static final String CONFIG_MULTITENANCY_ENABLED = CONFIG_MULTITENANCY_ROOT + ".enabled";
    public static final String CONFIG_MULTITENANCY_DATABASE_ENABLED = CONFIG_MULTITENANCY_ROOT + ".database.enabled";
    public static final String CONFIG_TENANT_STORE = CONFIG_MULTITENANCY_ROOT + ".store";
    /**
     * Common header - tenant id
     */
    public static final String HEADER_TENANT_ID = "Tenant";
    /**
     * Access token或jwt token存储tenant id 信息的key
     */
    public static final String TOKEN_TENANT_INFORMATION_KEY = "tenant";

    /**
     * Master租户的固定tenant id
     */
    public static final String MASTER_TENANT = "master";
    /**
     * 非多租户模式下的tenant id（用于代码适配兼容）
     */
    public static final String TENANT_ID_NON_MULTITENANCY_MODE = "default";
}
