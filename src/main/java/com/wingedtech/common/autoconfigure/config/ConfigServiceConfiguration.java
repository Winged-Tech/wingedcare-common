package com.wingedtech.common.autoconfigure.config;

import com.wingedtech.common.config.ConfigProvider;
import com.wingedtech.common.config.ConfigService;
import com.wingedtech.common.config.ConfigServiceProperties;
import com.wingedtech.common.config.Constants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(value = Constants.CONFIG_SERVICE_PROPERTIES_ROOT + ".enabled")
@EnableConfigurationProperties(ConfigServiceProperties.class)
@Import({ClassLoaderConfigProviderConfiguration.class, PropertiesConfigProviderConfiguration.class})
public class ConfigServiceConfiguration {
    @Bean
    ConfigService configService(ConfigProvider configProvider, ConfigServiceProperties configServiceProperties) {
        return new ConfigService(configProvider, configServiceProperties);
    }
}
