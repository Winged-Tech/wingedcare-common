package com.wingedtech.common.migrations;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties({DataMigrationProperties.class})
public class DataMigrationConfiguration {

    @Bean
    @ConditionalOnBean({DataProcessService.class})
    public DataMigrationService dataMigrationService(List<DataProcessService> services) {
        return new DataMigrationService(services);
    }

    @Bean
    @ConditionalOnBean({DataProcessService.class})
    public DataMigrationResource dataMigrationResource(List<DataProcessService> services) {
        return new DataMigrationResource(dataMigrationService(services));
    }
}
