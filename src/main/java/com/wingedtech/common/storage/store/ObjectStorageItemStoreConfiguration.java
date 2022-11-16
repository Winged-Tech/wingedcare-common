package com.wingedtech.common.storage.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ObjectStorageItemStoreProperties.class})
public class ObjectStorageItemStoreConfiguration {

    @Autowired
    ObjectStorageItemStoreProperties objectStorageItemStoreProperties;

    @Bean
    @ConditionalOnProperty(name = ObjectStorageItemStoreProperties.CONFIG_PROVIDER, prefix = ObjectStorageItemStoreProperties.CONFIG_ROOT, havingValue = "mock")
    ObjectStorageItemStore mockObjectStorageItemStore() {
        return new MockObjectStorageItemStoreImpl();
    }
}
