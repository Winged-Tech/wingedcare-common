package com.wingedtech.common.autoconfigure.security;

import com.wingedtech.common.security.PlatformSecurityProperties;
import com.wingedtech.common.security.oauth2.UaaSignatureVerifierClient;
import com.wingedtech.common.security.oauth2.config.OAuth2Properties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.client.RestTemplate;


/**
 * 自定义 Oauth2 资源服务配置
 *
 * @author jason
 */
@Configuration
@EnableConfigurationProperties({OAuth2Properties.class, PlatformSecurityProperties.class})
@ConditionalOnBean({ResourceServerConfiguration.class})
@Import(UaaSignatureVerifierClient.class)
public class PlatformSecurityConfiguration extends ResourceServerConfigurerAdapter implements Ordered {

    @Override
    public void configure(HttpSecurity http) {
        // 这里什么都不需要做, 覆盖基类的默认configure方法
    }

    @Bean
    @Qualifier("loadBalancedRestTemplate")
    public RestTemplate loadBalancedRestTemplate(RestTemplateCustomizer customizer) {
        RestTemplate restTemplate = new RestTemplate();
        customizer.customize(restTemplate);
        return restTemplate;
    }

    @Bean
    @Qualifier("vanillaRestTemplate")
    public RestTemplate vanillaRestTemplate() {
        return new RestTemplate();
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
