package com.wingedtech.common.multitenancy.cache;

import com.wingedtech.common.cache.config.GuavaCacheManageProperties;
import com.wingedtech.common.cache.guava.GuavaCacheManager;

/**
 * 多租户版本的GuavaCacheManager
 *
 * @author zhangyp
 */
public class MultiTenantGuavaCacheManager extends MultiTenantCacheManagerWrapper<GuavaCacheManager> {

    final private GuavaCacheManageProperties guavaCacheManageProperties;

    public MultiTenantGuavaCacheManager(GuavaCacheManageProperties guavaCacheManageProperties) {
        this.guavaCacheManageProperties = guavaCacheManageProperties;
    }

    @Override
    protected GuavaCacheManager buildCacheManager() {
        return guavaCacheManageProperties.buildGuavaCacheManager();
    }
}
