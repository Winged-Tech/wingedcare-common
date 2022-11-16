package com.wingedtech.common.storage.config;

import com.wingedtech.common.storage.ObjectStorageConfigService;
import com.wingedtech.common.storage.ObjectStorageItemPreprocessor;
import com.wingedtech.common.storage.multitenancy.ObjectStorageServiceMultiTenantConfiguration;
import com.wingedtech.common.storage.preprocessors.ObjectStorageItemPreprocessorConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * ObjectStorageService 配置相关configuration
 * @author taozhou
 * @date 2020/10/9
 */
@Configuration
@EnableConfigurationProperties({ObjectStorageResourceConfigProperties.class})
@Import({ObjectStorageServiceMultiTenantConfiguration.class, ObjectStorageItemPreprocessorConfiguration.class})
public class ObjectStorageServiceConfigConfiguration {

    @Bean
    public ObjectStorageConfigService objectStorageConfigService(ObjectStorageItemPreprocessor preprocessor, ObjectStorageResourceConfigProperties configProperties) {
        return new ObjectStorageConfigService(preprocessor, configProperties);
    }
}
