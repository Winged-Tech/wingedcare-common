package com.wingedtech.common.autoconfigure.config;

import com.wingedtech.common.config.ConfigProvider;
import com.wingedtech.common.config.Constants;
import com.wingedtech.common.config.providers.resources.ClassLoaderConfigProvider;
import com.wingedtech.common.config.providers.resources.ClassLoaderConfigProviderProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ClassLoaderConfigProviderProperties.class)
@ConditionalOnProperty(value = Constants.CONFIG_PROVIDER_KEY, havingValue = "resources")
@AutoConfigureBefore(ConfigServiceConfiguration.class)
@Slf4j
public class ClassLoaderConfigProviderConfiguration {

    @Bean
    ConfigProvider classLoaderConfigProvider() {
        log.info("Initializing ClassLoaderConfigProvider");
        return new ClassLoaderConfigProvider();
    }
}
