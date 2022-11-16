package com.wingedtech.common.storage.providers.file;

import com.wingedtech.common.autoconfigure.storage.ObjectStorageServiceConfiguration;
import com.wingedtech.common.storage.ObjectStorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.wingedtech.common.storage.config.ObjectStorageResourceConfigProperties.WINGED_OSS_PROVIDER;

@Configuration
@EnableConfigurationProperties(FileSystemStorageProperties.class)
@ConditionalOnProperty(value = WINGED_OSS_PROVIDER, havingValue = "file", matchIfMissing = false)
@AutoConfigureBefore(ObjectStorageServiceConfiguration.class)
public class FileSystemStorageConfiguration {
    @Autowired
    FileSystemStorageProperties fileSystemStorageProperties;

    @RefreshScope
    @Bean
    ObjectStorageProvider objectStorageProvider() {
        return new FileSystemStorageProvider(fileSystemStorageProperties);
    }
}
