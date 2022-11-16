package com.wingedtech.common.multitenancy.domain;

import com.wingedtech.common.multitenancy.Tenant;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 每一个租户的定义信息
 */
@Data
public class TenantInformation {
    /**
     * 租户唯一id
     */
    private String id;
    /**
     * 租户名称
     */
    private String name;
    /**
     * 该租户绑定的二级域名
     */
    private String subDomain;
    /**
     * 该租户的专属网关地址
     */
    private String apiGateway;
    /**
     * 为该租户绑定的域名列表
     */
    private List<String> domains;
    /**
     * 该租户的相关配置属性，例如数据库连接信息
     */
    private Map<String, String> properties;

    /**
     * 租户别名, 该别名可用于登录页面的显示或使用
     */
    private String alias;

    public boolean isMaster() {
        return Tenant.isMasterTenant(this.id);
    }

    public boolean hasSubDomain() {
        return StringUtils.isNotBlank(subDomain);
    }

    public boolean hasDomains() {
        return CollectionUtils.isNotEmpty(domains);}

    public String getProperty(String key) {
        if (properties != null) {
            return properties.get(key);
        }
        return null;
    }

    public boolean isAlias(String alias) {
        return this.alias != null && StringUtils.equals(this.alias, alias);
    }
}
