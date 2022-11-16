package com.wingedtech.common.multitenancy;

import com.wingedtech.common.multitenancy.domain.TenantInformation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents the information for current tenant
 * @author taozhou
 */
@Slf4j
@SuppressWarnings("unused")
public class Tenant {

    /**
     * 全局静态标记 - 标识当前app是否开启了多租户
     */
    private static boolean enabledMultitenancy = false;

    /**
     * A ThreadLocal variable storing the current tenant context.
     */
    private static final ThreadLocal<TenantContextHolder> currentTenant = new ThreadLocal<TenantContextHolder>() {
        @Override
        protected TenantContextHolder initialValue() {
            return new TenantContextHolder();
        }

        @Override
        public void set(TenantContextHolder value) {
            throw new UnsupportedOperationException("Thread tenant context cannot be modified after creation!");
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Thread tenant context cannot be modified after creation!");
        }
    };

    /**
     * 全局静态的TenantInformationService
     */
    private static TenantInformationService tenantInformationService;

    /**
     * Get the current tenant id
     * @return
     */
    public static String getCurrentTenantId() {
        return getCurrentTenantContext().getCurrentTenantId();
    }

    /**
     * 判断当前是否使用的是缺省tenantId (即客户端没有指定tenantId,或携带token)
     * @return
     */
    public static boolean getCurrentIsUsingDefaultTenantId() { return getCurrentTenantContext().getIsUsingDefaultTenant(); }

    /**
     * 如果当前有租户信息，返回当前租户id，否则返回默认的Master租户id Constants.MASTER_TENANT
     * @return
     */
    public static String getCurrentTenantIdOrMaster() {
        if (isEnabledMultitenancy()) {
            String currentTenantId = getCurrentTenantId();
            return currentTenantId != null ? currentTenantId : Constants.MASTER_TENANT;
        }
        else {
            return Constants.MASTER_TENANT;
        }

    }

    /**
     * 如果是多租户模式，后去当前tenant id，否则返回TENANT_ID_NON_MULTITENANCY_MODE
     * @return
     */
    public static String getCurrentTenantIdOrDefault() {
        if (isEnabledMultitenancy()) {
            return getCurrentTenantId();
        }
        else {
            return Constants.TENANT_ID_NON_MULTITENANCY_MODE;
        }
    }

    /**
     * 判断指定的tenant id是否为系统内部支持的合法的tenant id
     * @param tenantId
     * @return
     */
    public static boolean isValidTenantId(String tenantId) {
        return isMasterTenant(tenantId) || (tenantInformationService != null && tenantInformationService.hasTenant(tenantId));
    }

    /**
     * 获取当前租户是否为master租户
     * @return
     */
    public static boolean isMasterTenant() {
        return isCurrentTenantIdSet() && isMasterTenant(getCurrentTenantId());
    }

    public static boolean isMasterTenant(String tenantId) {
        return StringUtils.equals(tenantId, Constants.MASTER_TENANT);
    }

    /**
     * 获取当前是否启用了多租户功能
     * @return
     */
    public static boolean isEnabledMultitenancy() {
        return enabledMultitenancy;
    }

    /**
     * 获取当前租户的租户信息
     * @return
     */
    public static TenantInformation getCurrentTenant() {
        if (tenantInformationService == null) {
            throw new IllegalStateException("Tenant.tenantInformationService is null, please check configuration!");
        }
        if (!isCurrentTenantIdSet()) {
            throw new IllegalStateException("No tenant id information for the current context!");
        }
        final String currentTenantId = getCurrentTenantId();
        return tenantInformationService.getTenant(currentTenantId);
    }

    /**
     * 获取当前是否有设置tenant id
     * @return
     */
    public static boolean isCurrentTenantIdSet() {
        return StringUtils.isNotBlank(getCurrentTenantContext().getCurrentTenantId());
    }

    /**
     * Set the current tenant id
     * @param tenantId
     */
    public static void setCurrentTenantId(String tenantId) {
        setCurrentTenantId(tenantId, false);
    }

    public static void setCurrentTenantId(String tenantId, boolean isUsingDefault) {
        validateTenantId(tenantId);
        getCurrentTenantContext().setCurrentTenantId(tenantId);
        if (isUsingDefault) {
            getCurrentTenantContext().setUsingDefaultTenant();
        }
    }

    /**
     * Clear all information for current tenant
     */
    public static void clearCurrentTenant() {
        getCurrentTenantContext().clearCurrentTenant();
    }

    /**
     * 获取租户信息服务
     * @return
     */
    public static TenantInformationService getTenantInformationService() {
        return tenantInformationService;
    }

    /**
     * 设置租户信息服务
     * @param tenantInformationService
     */
    static synchronized void setTenantInformationService(TenantInformationService tenantInformationService) {
        log.info("Updating tenantInformationService...");
        if (Tenant.tenantInformationService != null) {
            log.warn("Trying to overwrite existing tenantInformationService!! Is this correct?");
        }
        Tenant.tenantInformationService = tenantInformationService;
        enabledMultitenancy = true;
    }

    static void clearTenantInformationService() {
        Tenant.tenantInformationService = null;
    }

    /**
     * 切换到临时租户环境，使用完后必须调用restoreTemporaryTenant恢复租户环境
     * @param tenantId
     */
    public static void switchToTemporaryTenant(String tenantId) {
        validateTenantId(tenantId);
        getCurrentTenantContext().switchToTemporaryTenant(tenantId);
    }

    private static TenantContextHolder getCurrentTenantContext() {
        return currentTenant.get();
    }

    /**
     * 清除当前临时租户
     */
    public static void restoreTemporaryTenant() {
        getCurrentTenantContext().releaseTemporaryTenant();
    }

    private static void validateTenantId(String tenantId) {
        if (!isValidTenantId(tenantId)) {
            throw new IllegalArgumentException("Invalid tenantId: " + tenantId);
        }
    }
}
