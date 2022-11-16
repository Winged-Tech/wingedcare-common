package com.wingedtech.common.autoconfigure.multitenancy;

import com.wingedtech.common.autoconfigure.multitenancy.cache.MultiTenantCacheAutoConfiguration;
import com.wingedtech.common.autoconfigure.multitenancy.oauth2.MultiTenancyOAuth2Configuration;
import com.wingedtech.common.autoconfigure.multitenancy.service.TenantInformationServiceConfiguration;
import com.wingedtech.common.multitenancy.Constants;
import com.wingedtech.common.multitenancy.config.MultiTenancyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(Constants.CONFIG_MULTITENANCY_ENABLED)
@EnableConfigurationProperties(MultiTenancyProperties.class)
@Import({TenantInformationServiceConfiguration.class, MultiTenancyOAuth2Configuration.class, MultiTenantCacheAutoConfiguration.class})
@Slf4j
public class MultiTenancyConfiguration {

}
