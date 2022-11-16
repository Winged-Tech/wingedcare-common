package com.wingedtech.common.multitenancy.config;

import com.wingedtech.common.config.ConfigProperties;
import com.wingedtech.common.config.ConfigPropertiesKey;
import lombok.Data;

/**
 * 租户通用外观配置
 * @author taozhou
 * @date 2021/2/19
 */
@ConfigPropertiesKey("tenant-generic-appearance")
@Data
public class TenantGenericAppearanceConfig implements ConfigProperties {
    /**
     * 租户 通用LOGO 图片地址, 可以是一个完整url, 也可以是一个OSS存储的相对路径
     */
    private String logoUrl;

    /**
     * 租户 登录页LOGO 图片地址 (可以是一个完整url, 也可以是一个OSS存储的相对路径)
     */
    private String loginLogoUrl;

    /**
     * 租户 菜单LOGO 图片地址 (可以是一个完整url, 也可以是一个OSS存储的相对路径)
     */
    private String menuLogoUrl;
}
