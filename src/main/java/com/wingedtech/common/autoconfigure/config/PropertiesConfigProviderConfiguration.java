package com.wingedtech.common.autoconfigure.config;


import com.wingedtech.common.config.ConfigProvider;
import com.wingedtech.common.config.Constants;
import com.wingedtech.common.config.providers.properties.PropertiesConfigProvider;
import com.wingedtech.common.config.providers.properties.PropertiesConfigProviderProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties(PropertiesConfigProviderProperties.class)
@ConditionalOnProperty(value = Constants.CONFIG_PROVIDER_KEY, havingValue = "properties")
@AutoConfigureBefore(ConfigServiceConfiguration.class)
@Slf4j
public class PropertiesConfigProviderConfiguration {
    @Bean
    ConfigProvider propertiesConfigProvider(Environment environment) {
        log.info("Initializing PropertiesConfigProvider");
        return new PropertiesConfigProvider(Binder.get(environment));
    }
}
