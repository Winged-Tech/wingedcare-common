package com.wingedtech.common.autoconfigure.multitenancy.service;

import com.wingedtech.common.multitenancy.service.information.TenantInformationStore;
import com.wingedtech.common.multitenancy.service.information.properties.TenantInformationProperties;
import com.wingedtech.common.multitenancy.service.information.properties.TenantInformationPropertiesStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.wingedtech.common.multitenancy.Constants.CONFIG_TENANT_STORE;

@Configuration
@ConditionalOnProperty(value = CONFIG_TENANT_STORE, havingValue = "properties", matchIfMissing = true)
@EnableConfigurationProperties(TenantInformationProperties.class)
@AutoConfigureBefore({TenantInformationServiceConfiguration.class})
@Slf4j
public class TenantInformationPropertiesStoreConfiguration {

    @Bean
    public TenantInformationStore tenantInformationStore(TenantInformationProperties tenantInformationProperties) {
        log.info("Initializing TenantInformationPropertiesStore");
        return new TenantInformationPropertiesStore(tenantInformationProperties);
    }
}
