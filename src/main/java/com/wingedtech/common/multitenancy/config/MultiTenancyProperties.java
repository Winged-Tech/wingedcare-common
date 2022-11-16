package com.wingedtech.common.multitenancy.config;

import com.wingedtech.common.multitenancy.Constants;
import com.wingedtech.common.multitenancy.domain.TenantInformation;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

import static com.wingedtech.common.multitenancy.Constants.MASTER_TENANT;

/**
 * 多租户配置信息
 */
@Data
@ConfigurationProperties(Constants.CONFIG_MULTITENANCY_ROOT)
public class MultiTenancyProperties {
    public static final boolean ENABLE_SUB_DOMAIN_FILTER = false;
    /***
     * 如果设置了default tenant，则在没有tenant信息的时候会使用default tenant
     */
    private String defaultTenant = MASTER_TENANT;

    /**
     * 是否启用多租户
     */
    private boolean enabled = false;

    /**
     * 是否允许从请求域名里匹配租户
     */
    private boolean enableDomainMatching = false;

    /**
     * 是否开启用户名识别租户ID或者租户别名信息
     * <pre>
     *    example: username@tenantIdOrAlias
     * <pre/>
     */
    private boolean enableUsernameMatching = false;

    private String driver;

    /**
     * 全局的多租户配置信息定义
     */
    private List<TenantInformation> tenants;

    /**
     * 多租户数据库相关配置
     */
    private MultiTenancyDataProperties data;

    /**
     * MultiTenantFilter相关配置
     */
    private MultiTenancyFilterProperties filter;

    public boolean isDefaultTenantSet() {
        return StringUtils.isNotBlank(defaultTenant);
    }
}
