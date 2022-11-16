package com.wingedtech.common.client.uaa;

import com.wingedtech.common.client.FeignClientProperties;
import com.wingedtech.common.multitenancy.Tenant;
import com.wingedtech.common.multitenancy.oauth2.MultiTenantOAuth2ClientContext;
import com.wingedtech.common.multitenancy.oauth2.MultiTenantOAuth2FeignRequestInterceptor;
import feign.RequestInterceptor;
import io.github.jhipster.security.uaa.LoadBalancedResourceDetails;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;

import java.io.IOException;

@EnableConfigurationProperties({FeignClientProperties.class})
public class OAuth2InterceptedFeignConfiguration {

    private final LoadBalancedResourceDetails loadBalancedResourceDetails;

    private final FeignClientProperties feignClientProperties;

    public OAuth2InterceptedFeignConfiguration(LoadBalancedResourceDetails loadBalancedResourceDetails, FeignClientProperties feignClientProperties) {
        this.loadBalancedResourceDetails = loadBalancedResourceDetails;
        this.feignClientProperties = feignClientProperties;
    }

    @Bean(name = "oauth2RequestInterceptor")
    public RequestInterceptor getOAuth2RequestInterceptor() throws IOException {
        if (Tenant.isEnabledMultitenancy()) {
            return new MultiTenantOAuth2FeignRequestInterceptor(new MultiTenantOAuth2ClientContext(), loadBalancedResourceDetails, feignClientProperties.getAutoWarmUp());
        }
        else {
            return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), loadBalancedResourceDetails);
        }
    }
}
