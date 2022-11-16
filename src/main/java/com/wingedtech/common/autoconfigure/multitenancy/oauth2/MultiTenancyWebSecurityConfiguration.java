package com.wingedtech.common.autoconfigure.multitenancy.oauth2;

import com.wingedtech.common.multitenancy.Constants;
import com.wingedtech.common.multitenancy.config.MultiTenancyProperties;
import com.wingedtech.common.multitenancy.oauth2.MultiTenantFilterConfigurer;
import com.wingedtech.common.security.ExtensionOauth2Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Slf4j
@Configuration
@ConditionalOnProperty(Constants.CONFIG_MULTITENANCY_ENABLED)
@ConditionalOnBean(ResourceServerConfiguration.class)
public class MultiTenancyWebSecurityConfiguration extends ResourceServerConfigurerAdapter implements Ordered {

    @Autowired
    private MultiTenancyProperties multiTenancyProperties;

    /**
     * 为了减少依赖注入, 临时采用属性绑定的方法
     */
    @Value("${" + ExtensionOauth2Constants.PLATFORM_TOKEN_STORE + "}")
    private String tokenStore;

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        log.info("Configuring MultiTenantFilter...");
        MultiTenantFilterConfigurer configurer = new MultiTenantFilterConfigurer(multiTenancyProperties, tokenStore);
        http.apply(configurer);
    }
}
