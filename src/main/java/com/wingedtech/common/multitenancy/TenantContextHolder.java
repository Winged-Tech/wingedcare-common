package com.wingedtech.common.multitenancy;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

/**
 * Used to hold the multi tenant context of a single thread.
 */
@Slf4j
public class TenantContextHolder {
    private String currentTenantId;
    private String temporaryTenantId;
    /**
     * 记录当前是否使用的是缺省tenantId (即客户端没有指定tenantId,或携带token)
     */
    private Boolean isUsingDefaultTenant;

    /**
     * 获取当前租户id
     * @return
     */
    public String getCurrentTenantId() {
        return this.temporaryTenantId != null ? this.temporaryTenantId : this.currentTenantId;
    }

    /**
     * 设置当前租户id，此操作会清除当前的临时租户
     * @param tenantId
     * @return
     */
    public void setCurrentTenantId(String tenantId) {
        this.currentTenantId = tenantId;
        this.temporaryTenantId = null;
    }

    public void setUsingDefaultTenant() {
        this.isUsingDefaultTenant = Boolean.TRUE;
    }

    public boolean getIsUsingDefaultTenant() {
        return BooleanUtils.isTrue(isUsingDefaultTenant);
    }

    /**
     * 切换到指定的临时租户
     * @param temporaryTenantId
     */
    public synchronized void switchToTemporaryTenant(String temporaryTenantId) {
        if (this.temporaryTenantId != null && !this.temporaryTenantId.equals(temporaryTenantId)) {
            log.error("[TenantContextHolder] Unable to switch to temporary tenant context [{}] while we're still in a temporary tenant context [{}]!", temporaryTenantId, this.temporaryTenantId);
            throw new IllegalStateException();
        }
        this.temporaryTenantId = temporaryTenantId;
    }

    /**
     * 清除当前的临时租户
     */
    public void releaseTemporaryTenant() {
        this.temporaryTenantId = null;
    }

    /**
     * 清除所有租户信息
     */
    public void clearCurrentTenant() {
        this.temporaryTenantId = null;
        this.currentTenantId = null;
        this.isUsingDefaultTenant = null;
    }
}
