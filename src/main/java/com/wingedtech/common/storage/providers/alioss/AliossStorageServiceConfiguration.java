package com.wingedtech.common.storage.providers.alioss;

import com.wingedtech.common.storage.ObjectStorageProvider;
import com.wingedtech.common.autoconfigure.storage.ObjectStorageServiceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.wingedtech.common.storage.config.ObjectStorageResourceConfigProperties.WINGED_OSS_PROVIDER;

@Configuration
@EnableConfigurationProperties({AliossStorageServiceProperties.class})
@ConditionalOnProperty(value = WINGED_OSS_PROVIDER, havingValue = "alioss", matchIfMissing = true)
@AutoConfigureBefore(ObjectStorageServiceConfiguration.class)
public class AliossStorageServiceConfiguration {
    @Autowired
    private AliossStorageServiceProperties aliossStorageServiceProperties;

    @Bean
    ObjectStorageProvider provider() {
        return new AliossStorageProvider(aliossStorageServiceProperties);
    }

//    @Bean
//    ObjectStorageService objectStorageService(ObjectStorageResourceConfigProperties configProperties) {
//        return new ObjectStorageService(provider(), configProperties);
//    }
}
