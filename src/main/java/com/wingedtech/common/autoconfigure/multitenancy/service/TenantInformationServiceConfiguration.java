package com.wingedtech.common.autoconfigure.multitenancy.service;

import com.wingedtech.common.config.ConfigService;
import com.wingedtech.common.multitenancy.TenantInformationService;
import com.wingedtech.common.multitenancy.service.information.TenantInformationStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({TenantInformationPropertiesStoreConfiguration.class})
@Slf4j
public class TenantInformationServiceConfiguration {

    @Bean
    public TenantInformationService tenantInformationService(TenantInformationStore tenantInformationStore, ConfigService configService) {
        log.info("Initializing TenantInformationService");
        return new TenantInformationService(tenantInformationStore, configService);
    }
}
