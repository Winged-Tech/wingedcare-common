package com.wingedtech.common.keycloak.client;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author taozhou
 * @date 2021/7/4
 */
@Configuration
@Import({OAuth2InterceptedFeignConfiguration.class})
public class KeycloakFeignClientConfiguration {
}
