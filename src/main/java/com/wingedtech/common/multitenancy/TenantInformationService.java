package com.wingedtech.common.multitenancy;

import com.wingedtech.common.config.ConfigService;
import com.wingedtech.common.multitenancy.config.TenantGenericAppearanceConfig;
import com.wingedtech.common.multitenancy.domain.TenantInformation;
import com.wingedtech.common.multitenancy.service.information.TenantInformationStore;
import com.wingedtech.common.multitenancy.util.TemporaryTenantContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 用于获取租户信息的全局服务
 */
@Slf4j
public class TenantInformationService implements InitializingBean {

    private final TenantInformationStore tenantInformationStore;

    private final ConfigService configService;

    private List<TenantInformation> tenants;

    private Set<String> tenantIds = new HashSet<>();

    private Map<String, TenantInformation> tenantsMap = new HashMap<>();

    /**
     * 域名到租户信息的映射关系缓存
     */
    private Map<String, TenantInformation> domainTenantCache = new ConcurrentHashMap<>();

    public TenantInformationService(TenantInformationStore tenantInformationStore, ConfigService configService) {
        this.tenantInformationStore = tenantInformationStore;
        this.configService = configService;
    }

    /**
     * 获取只读版本的所有租户信息list
     *
     * @return
     */
    public List<TenantInformation> getTenants() {
        return Collections.unmodifiableList(tenants);
    }

    /**
     * 获取只读版本的所有租户id set
     *
     * @return
     */
    public Set<String> getTenantIds() {
        return Collections.unmodifiableSet(tenantIds);
    }

    @Override
    public void afterPropertiesSet() {
        this.init();
    }

    public void init() {
        log.info("Initializing tenant information service.");
        log.info("Current tenant information store is {}", tenantInformationStore.getClass().getName());
        tenants = tenantInformationStore.getAll();
        log.info("Current tenants information: {}", tenants);
        if (CollectionUtils.isEmpty(tenants)) {
            log.error("No tenant information configured!");
        } else {
            buildTenantsMap();
        }
        Tenant.setTenantInformationService(this);
    }

    private void buildTenantsMap() {
        if (tenants != null) {
            tenantsMap.clear();
            domainTenantCache.clear();
            tenantIds.clear();
            for (TenantInformation tenant : tenants) {
                final String tenantId = tenant.getId();
                tenantIds.add(tenantId);
                tenantsMap.put(tenantId, tenant);
            }
        }
    }

    /**
     * 判断是否包含指定的租户id，可用来判定指定租户id是否合法
     *
     * @param tenantId
     * @return
     */
    public boolean hasTenant(String tenantId) {
        return tenantsMap.containsKey(tenantId);
    }

    /**
     * 获取指定id的租户信息
     *
     * @param tenantId
     * @return
     */
    public TenantInformation getTenant(String tenantId) {
        return tenantsMap.get(tenantId);
    }

    /**
     * 根据完整域名判定租户
     *
     * @param domain
     * @return
     */
    public TenantInformation getTenantByDomain(String domain) {
        TenantInformation tenant = domainTenantCache.get(domain);
        if (tenant == null) {
            for (TenantInformation tenantInformation : tenants) {
                if (tenantInformation.hasDomains()) {
                    for (String tenantDomain : tenantInformation.getDomains()) {
                        if (domain.contains(tenantDomain)) {
                            tenant = tenantInformation;
                            domainTenantCache.put(domain, tenant);
                            break;
                        }
                    }
                }
            }
        }
        return tenant;
    }

    /**
     * 根据租户别名获取租户信息
     *
     * @param alias
     * @return TenantInformation
     */
    public TenantInformation getTenantByAlias(String alias) {
        for (TenantInformation tenantInformation : tenants) {
            if (tenantInformation.isAlias(alias)) {
                return tenantInformation;
            }
        }
        return null;
    }

    public TenantInformation getTenantBySubDomain(String subDomain) {
        TenantInformation tenant = domainTenantCache.get(subDomain);
        if (tenant == null) {
            for (TenantInformation tenantInformation : tenants) {
                if (tenantInformation.hasSubDomain() && subDomain.contains(tenantInformation.getSubDomain())) {
                    tenant = tenantInformation;
                }
            }
            if (tenant != null) {
                domainTenantCache.put(subDomain, tenant);
            }
        }
        return tenant;
    }

    /**
     * 为每一个租户执行指定的action(自动使用TemporaryTenantContext切换当前租户上下文)
     *
     * @param action 接收一个String参数, 参数值为当前tenantId
     */
    public void forEachTenant(Consumer<String> action) {
        getTenantIds().forEach(tenantId -> {
            try (TemporaryTenantContext context = new TemporaryTenantContext(tenantId)) {
                action.accept(tenantId);
            }
        });
    }

    /**
     * 获取当前租户的TenantGenericAppearanceConfig
     *
     * @return
     */
    public TenantGenericAppearanceConfig getTenantGenericAppearanceConfig() {
        return configService.getConfig(TenantGenericAppearanceConfig.class);
    }
}
