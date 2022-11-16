package com.wingedtech.common.multitenancy;

import com.wingedtech.common.multitenancy.domain.TenantInformation;
import com.wingedtech.common.multitenancy.domain.TenantPropertyKeys;

import java.util.function.Consumer;

/**
 * 用于获取当前租户信息的一些helper方法
 */
public class TenantInformationHelper {
    /**
     * 获取当前租户的mongo数据库uri
     * @return
     */
    public static String getMongoUri() {
        return getCurrentTenantProperty(TenantPropertyKeys.MONGO_URI);
    }

    /**
     * 获取当前租户的mongo数据库名称
     * @return
     */
    public static String getMongoDatabase() {
        return getCurrentTenantProperty(TenantPropertyKeys.MONGO_DATABASE);
    }


    /**
     * 获取当前租户的mysql数据库uri
     * @return
     */
    public static String getMySqlUri() {
        return getCurrentTenantProperty(TenantPropertyKeys.MYSQL_URI);
    }

    /**
     * 获取当前租户的指定property
     * @param key
     * @return
     */
    public static String getCurrentTenantProperty(String key) {
        return Tenant.getCurrentTenant().getProperty(key);
    }

    /**
     * 获取指定TenantInformation的database名称
     * @return
     */
    public static String getMongoDatabase(String tenantId) {
        final TenantInformation tenantInformation = getTenantInformation(tenantId);
        return tenantInformation.getProperty(TenantPropertyKeys.MONGO_DATABASE);
    }

    public static String getTenantProperty(String tenantId, String key) {
        return getTenantInformation(tenantId).getProperty(key);
    }

    public static TenantInformation getTenantInformation(String tenantId) {
        TenantInformationService tenantInformationService = getTenantInformationService();
        final TenantInformation tenantInformation = tenantInformationService.getTenant(tenantId);
        if (tenantInformation == null) {
            throw new IllegalArgumentException("Unable to find tenant information for " + tenantId);
        }
        return tenantInformation;
    }

    public static TenantInformationService getTenantInformationService() {
        TenantInformationService tenantInformationService = Tenant.getTenantInformationService();
        if (tenantInformationService == null) {
            throw new IllegalStateException("TenantInformationService is null, multitenancy is not initialized!");
        }
        return tenantInformationService;
    }

    /**
     * 为每一个租户执行指定的action(自动使用TemporaryTenantContext切换当前租户上下文)
     * @param action 接收一个String参数, 参数值为当前tenantId
     */
    public static void forEachTenant(Consumer<String> action) {
        getTenantInformationService().forEachTenant(action);
    }

    /**
     * 获取TenantInformation中的MongoUri
     */
    public static String getMongoUri(String tenantId){
        final TenantInformation tenantInformation = getTenantInformation(tenantId);
        return tenantInformation.getProperty(TenantPropertyKeys.MONGO_URI);
    }
}
