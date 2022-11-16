package com.wingedtech.common.keycloak.security;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

/**
 * @author taozhou
 * @date 2021/7/4
 */
@Configuration
public class KeycloakCommonSecurityConfiguration {

    @Bean
    AuthorizationHeaderUtil authorizationHeaderUtil(OAuth2AuthorizedClientService clientService, RestTemplateBuilder restTemplateBuilder) {
        return new AuthorizationHeaderUtil(clientService, restTemplateBuilder);
    }
}
