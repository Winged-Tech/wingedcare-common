package com.wingedtech.common.security.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@RefreshScope
@ConditionalOnClass(ResourceServerConfiguration.class)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableConfigurationProperties(HttpSecurityProperties.class)
@Order
@Slf4j
public class HttpSecurityConfiguration extends ResourceServerConfigurerAdapter {

    private HttpSecurityProperties httpSecurityProperties;

    public HttpSecurityConfiguration(HttpSecurityProperties httpSecurityProperties) {
        this.httpSecurityProperties = httpSecurityProperties;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        if (httpSecurityProperties.hasRules()) {
            log.info("Configuring http security with properties: {}", httpSecurityProperties);
            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
            registry = HttpSecurityConfigurer.configure(registry, httpSecurityProperties.getRules(), httpSecurityProperties.getTemplates());
        }
    }
}
