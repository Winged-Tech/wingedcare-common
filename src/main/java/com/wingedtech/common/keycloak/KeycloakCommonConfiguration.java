package com.wingedtech.common.keycloak;

import com.wingedtech.common.keycloak.client.KeycloakFeignClientConfiguration;
import com.wingedtech.common.keycloak.security.KeycloakCommonSecurityConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author taozhou
 * @date 2021/7/4
 */
@Configuration
@Import({KeycloakFeignClientConfiguration.class, KeycloakCommonSecurityConfiguration.class})
public class KeycloakCommonConfiguration {
}
