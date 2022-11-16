package com.wingedtech.common.multitenancy.util;

import com.wingedtech.common.multitenancy.Tenant;
import org.springframework.util.Assert;

/**
 * AutoCloseable的临时租户环境切换
 */
public class TemporaryTenantContext implements AutoCloseable {

    private boolean checkMultitenantEnabled;
    private boolean tenantSwitched = false;

    public TemporaryTenantContext(String tenantId) {
        this(tenantId, false);
    }

    public TemporaryTenantContext(String tenantId, boolean checkMultitenantEnabled) {
        this.checkMultitenantEnabled = checkMultitenantEnabled;
        if (!this.checkMultitenantEnabled || Tenant.isEnabledMultitenancy()) {
            Assert.notNull(tenantId, "tenantId cannot be null!");
            Tenant.switchToTemporaryTenant(tenantId);
            tenantSwitched = true;
        }
    }

    /**
     * 获取一个指定tenantId的TemporaryTenantContext, 并且需要多租户功能为开启状态(如果未开启将会产生异常)
     * @param tenantId
     * @return
     */
    public static TemporaryTenantContext forTenantAndRequireMultiTenant(String tenantId) {
        return new TemporaryTenantContext(tenantId, false);
    }

    /**
     * 获取一个指定tenantId的TemporaryTenantContext, 不强制多租户功能为开启状态, 如果当前未开启多租户则不做任何事情
     * @param tenantId
     * @return
     */
    public static TemporaryTenantContext forTenantAndOptionalMultiTenant(String tenantId) {
        return new TemporaryTenantContext(tenantId, true);
    }

    @Override
    public void close() {
        if (tenantSwitched) {
            Tenant.restoreTemporaryTenant();
        }
    }
}
