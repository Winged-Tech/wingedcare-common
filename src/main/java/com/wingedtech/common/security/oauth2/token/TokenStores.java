package com.wingedtech.common.security.oauth2.token;

import org.apache.commons.lang3.StringUtils;

/**
 * @author taozhou
 * @date 2021/6/17
 */
public class TokenStores {
    public static final String MONGODB = "mongodb";
    public static final String JWT = "jwt";
    public static final String JDBC = "jdbc";

    public static boolean isMongoDBTokenStore(String storeName) {
        return StringUtils.equals(MONGODB, storeName);
    }

    public static boolean isJwtTokenStore(String storeName) {
        return StringUtils.equals(JWT, storeName);
    }

    public static boolean isJdbcTokenStore(String storeName) {
        return StringUtils.equals(JDBC, storeName);
    }
}
