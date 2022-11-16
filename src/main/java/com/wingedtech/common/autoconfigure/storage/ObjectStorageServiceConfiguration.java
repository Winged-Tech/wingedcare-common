package com.wingedtech.common.autoconfigure.storage;

import com.wingedtech.common.storage.ObjectStorageConfigService;
import com.wingedtech.common.storage.ObjectStorageProvider;
import com.wingedtech.common.storage.ObjectStorageService;
import com.wingedtech.common.storage.config.ObjectStorageResourceConfigProperties;
import com.wingedtech.common.storage.config.ObjectStorageServiceConfigConfiguration;
import com.wingedtech.common.storage.providers.alioss.AliossStorageServiceConfiguration;
import com.wingedtech.common.storage.providers.file.FileSystemStorageConfiguration;
import com.wingedtech.common.storage.providers.inspuross.LangChaoStorageServiceConfiguration;
import com.wingedtech.common.storage.rest.CommonStorageResource;
import com.wingedtech.common.storage.rest.DirectDownloadResource;
import com.wingedtech.common.storage.rest.DirectUploadResource;
import com.wingedtech.common.storage.rest.DynamicConfigurationInternalResource;
import com.wingedtech.common.storage.store.ObjectStorageItemStore;
import com.wingedtech.common.storage.store.ObjectStorageItemStoreConfiguration;
import com.wingedtech.common.storage.store.ObjectStorageResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ObjectStorageServiceConfigConfiguration.class, ObjectStorageItemStoreConfiguration.class, AliossStorageServiceConfiguration.class, FileSystemStorageConfiguration.class, LangChaoStorageServiceConfiguration.class})
public class ObjectStorageServiceConfiguration {

    @Autowired ObjectStorageProvider provider;
    @Autowired ObjectStorageResourceConfigProperties configProperties;
    @Autowired ObjectStorageItemStore store;
    @Autowired ObjectStorageConfigService objectStorageConfigService;

    @Bean
    public ObjectStorageService objectStorageService() {
        return new ObjectStorageService(provider, configProperties, store, objectStorageConfigService);
    }

    @Bean
    public ObjectStorageResource objectStorageResource() {
        return new ObjectStorageResource(objectStorageService(), store);
    }

    @Bean
    @ConditionalOnProperty(value = "enable-common-api", prefix = ObjectStorageResourceConfigProperties.WINGED_OSS_ROOT)
    public CommonStorageResource commonStorageResource() {
        return new CommonStorageResource(objectStorageService());
    }

    @Bean
    @ConditionalOnProperty(value = "enable-common-refresh-api", prefix = ObjectStorageResourceConfigProperties.WINGED_OSS_ROOT)
    public DynamicConfigurationInternalResource dynamicConfigurationInternalResource() {
        return new DynamicConfigurationInternalResource();
    }

    @Bean
    @ConditionalOnProperty(value = ObjectStorageResourceConfigProperties.ENABLE_DIRECT_UPLOAD, prefix = ObjectStorageResourceConfigProperties.WINGED_OSS_CONFIG, havingValue = "true")
    public DirectUploadResource directUploadResource() {
        return new DirectUploadResource(objectStorageService());
    }

    @Bean
    @ConditionalOnProperty(value = ObjectStorageResourceConfigProperties.ENABLE_DIRECT_DOWNLOAD, prefix = ObjectStorageResourceConfigProperties.WINGED_OSS_CONFIG, havingValue = "true")
    public DirectDownloadResource directDownloadResource() {
        return new DirectDownloadResource(objectStorageService());
    }
}
