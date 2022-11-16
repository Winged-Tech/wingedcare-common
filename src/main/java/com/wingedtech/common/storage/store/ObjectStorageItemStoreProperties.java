package com.wingedtech.common.storage.store;

import com.wingedtech.common.storage.config.ObjectStorageResourceConfigProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(ObjectStorageItemStoreProperties.CONFIG_ROOT)
public class ObjectStorageItemStoreProperties {
    public static final String CONFIG_ROOT = ObjectStorageResourceConfigProperties.WINGED_OSS_CONFIG + ".store";
    public static final String CONFIG_PROVIDER = "provider";
    public static final String MOCK_PROVIDER = "mock";
    /**
     * 是否强制登录才可以操作store
     */
    private boolean forceLogin = false;
    /**
     * 当当前上下文没有用户身份信息时, 默认使用的userLogin (用于createdBy以及lastModifiedBy字段)
     */
    private String defaultLogin = "system";
    /**
     * ObjectStorageService store的存储provider, 默认为mock
     */
    private String provider = MOCK_PROVIDER;
}
