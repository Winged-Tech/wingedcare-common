package com.wingedtech.common.streams.check;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.binder.BinderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(CheckingStreamProperties.class)
@ConditionalOnBean(value = {BinderFactory.class})
@ConditionalOnProperty(value = CheckingStreamProperties.CONFIG_ROOT + ".enabled", havingValue = "true", matchIfMissing = true)
@Import({CheckingStreamBindingConfiguration.class})
public class CheckingStreamConfiguration {

    @Autowired private CheckingStreamProperties properties;
    @Autowired private CheckingStream checkingStream;

    @Bean
    public CheckingStreamService checkingStreamService() {
        return new CheckingStreamService(properties, checkingStream);
    }

    @Bean
    public CheckingStreamResource checkingStreamResource() {
        return new CheckingStreamResource(checkingStreamService());
    }
}
