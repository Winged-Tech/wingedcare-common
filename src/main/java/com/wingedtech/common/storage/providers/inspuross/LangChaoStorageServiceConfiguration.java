package com.wingedtech.common.storage.providers.inspuross;

import com.wingedtech.common.storage.ObjectStorageProvider;
import com.wingedtech.common.autoconfigure.storage.ObjectStorageServiceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.wingedtech.common.storage.config.ObjectStorageResourceConfigProperties.WINGED_OSS_PROVIDER;

/**
 * @author ssy
 * @date 2020/11/25 15:53
 */

@Configuration
@EnableConfigurationProperties({LangChaoStorageServiceProperties.class})
@ConditionalOnProperty(value = WINGED_OSS_PROVIDER, havingValue = "inspuross")
@AutoConfigureBefore(ObjectStorageServiceConfiguration.class)
public class LangChaoStorageServiceConfiguration {

    @Autowired
    private LangChaoStorageServiceProperties langChaoStorageServiceProperties;

    @Bean
    ObjectStorageProvider provider() {
        return new LangChaoStorageProvider(langChaoStorageServiceProperties);
    }
}
