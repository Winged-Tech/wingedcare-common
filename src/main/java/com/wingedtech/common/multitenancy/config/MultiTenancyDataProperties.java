package com.wingedtech.common.multitenancy.config;

import com.wingedtech.common.multitenancy.Constants;
import lombok.Data;

/**
 * 多租户数据库相关配置
 */
@Data
public class MultiTenancyDataProperties {
    /**
     * 当前的配置的配置项路径 - winged.multitenancy.data
     */
    public static final String CONFIGURATION_ROOT = Constants.CONFIG_MULTITENANCY_ROOT + ".data";
    public static final String PROPERTY_ENABLE_DB_MIGRATION = CONFIGURATION_ROOT + ".enableDbMigration";
    public static final String PROPERTY_RUN_DB_MIGRATION_ON_STARTUP = CONFIGURATION_ROOT + ".runDbMigrationOnStartup";
    /**
     * 租户数据库名前缀
     */
    private String databasePrefix;

    /**
     * 租户数据库名后缀(为空时默认为服务名)
     */
    private String databaseSuffix;

    /**
     * db migration的扫描目录
     */
    private String migrationScanPackage;
    /**
     * 是否启用多租户的 db migration（默认开启）
     */
    private boolean enableDbMigration = true;
    /**
     * 当租户的db migration执行失败时，是否停止启动
     */
    private boolean stopWhenMigrationFails = true;
    /**
     * 如果为true，则会将多租户的数据库操作Template（例如，MongoTemplate）注册为primary
     * 有的服务存在旧的不合理代码，在service中直接注入并使用了MongoTemplate，这种情况需要打开此开关，使得所有MongoTemplate都经过了多租户处理。
     */
    private boolean useTenantTemplateAsPrimary = false;

    /**
     * 是否所有租户都共用master db client的同一db连接, 租户之间的db连接不隔离
     * 默认为false (在自动化测试中可以置为 true)
     */
    private boolean allTenantsUseMasterClient = false;
    /**
     * 是否在服务启动时自动运行所有租户的db migration
     * 默认为true
     */
    private boolean runDbMigrationOnStartup = true;
}
