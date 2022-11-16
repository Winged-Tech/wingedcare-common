package com.wingedtech.common.autoconfigure.multitenancy.oauth2.authorization;

import com.wingedtech.common.multitenancy.MultiTenantFilter;
import com.wingedtech.common.multitenancy.config.MultiTenancyProperties;
import com.wingedtech.common.multitenancy.oauth2.MultiTenantFilterConfigurer;
import com.wingedtech.common.multitenancy.oauth2.authorization.TenantTokenEnhancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

/**
 * OAuth2 authorization server (例如uaa）专用配置，用于配置tokenEndpoint
 */
@Slf4j
@Configuration
@ConditionalOnBean(AuthorizationServerSecurityConfiguration.class)
public class MultiTenancyAuthorizationServerSecurityConfiguration extends AuthorizationServerConfigurerAdapter implements Ordered {

    @Autowired
    private MultiTenancyProperties multiTenancyProperties;

    @Bean
    TenantTokenEnhancer tenantTokenEnhancer() {
        log.info("Creating TenantTokenEnhancer");
        return new TenantTokenEnhancer();
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer.addTokenEndpointAuthenticationFilter(new MultiTenantFilter(multiTenancyProperties, MultiTenantFilterConfigurer.buildExcludingRequestMatcher(multiTenancyProperties.getFilter()), MultiTenantFilterConfigurer.buildIgnoringTokenRequestMatcher(multiTenancyProperties.getFilter())));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
