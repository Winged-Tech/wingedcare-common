package com.wingedtech.common.multitenancy.cache;

import com.wingedtech.common.multitenancy.Constants;
import com.wingedtech.common.multitenancy.Tenant;
import com.wingedtech.common.multitenancy.util.MultiTenantValueWrapper;
import com.wingedtech.common.multitenancy.util.TemporaryTenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
public abstract class MultiTenantCacheManagerWrapper<T extends CacheManager> implements CacheManager, InitializingBean {
    private final MultiTenantValueWrapper<T> cacheManagerMap = new MultiTenantValueWrapper<>();

    @Override
    public void afterPropertiesSet() {
        initializeCaches();
    }

    /**
     * Initialize the static configuration of caches.
     * <p>Triggered on startup through {@link #afterPropertiesSet()};
     * can also be called to re-initialize at runtime.
     * @since 4.2.2
     */
    public void initializeCaches() {
        log.info("[MultiTenantCacheManagerWrapper] Initializing caches");
        initializeTenant(Constants.MASTER_TENANT);
        if (Tenant.getTenantInformationService() != null) {
            log.info("[MultiTenantCacheManagerWrapper] Initializing cache for tenants");
            final Set<String> tenantIds = Tenant.getTenantInformationService().getTenantIds();
            for (String tenantId : tenantIds) {
                initializeTenant(tenantId);
            }
        }
        else {
            log.warn("[MultiTenantCacheManagerWrapper] TenantInformationService is not initialized, tenant caches will be initialized on demand");
        }
    }

    private void initializeTenant(String tenantId) {
        try (TemporaryTenantContext context = new TemporaryTenantContext(tenantId)) {
            if (!cacheManagerMap.isPresent()) {
                cacheManagerMap.set(buildCacheManager());
            }
            final CacheManager cacheManager = getCacheManager();
            if (cacheManager instanceof AbstractCacheManager) {
                ((AbstractCacheManager) cacheManager).initializeCaches();
            }
        }
    }

    @Override
    @Nullable
    public Cache getCache(String name) {
        return getCacheManager().getCache(name);
    }

    @Override
    public Collection<String> getCacheNames() {
        return getCacheManager().getCacheNames();
    }

    /**
     * Applies the specified consumer on each CacheManager for each tenant
     * @param consumer
     */
    public void forEachTenant(Consumer<T> consumer) {
        cacheManagerMap.forEachTenant(consumer);
    }

    private CacheManager getCacheManager() {
        if (!cacheManagerMap.isPresent()) {
            initializeTenant(Tenant.getCurrentTenantIdOrMaster());
        }
        return cacheManagerMap.get();
    }

    protected abstract T buildCacheManager();
}
