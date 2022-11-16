package com.wingedtech.common.multitenancy.domain;

/**
 * 租户properties的相关key
 */
public class TenantPropertyKeys {
    /**
     * mongo 连接字符串
     */
    public static final String MONGO_URI = "mongo";
    /**
     * mongo 数据库名称（可留空）
     */
    public static final String MONGO_DATABASE = "mongoDatabase";
    /**
     * mysql 连接字符串
     */
    public static final String MYSQL_URI = "mysql";
}
