package com.wingedtech.common.autoconfigure.keycloak;

import com.wingedtech.common.keycloak.KeycloakCommonConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author taozhou
 * @date 2021/7/4
 */
@Configuration
@Import({KeycloakCommonConfiguration.class})
public class KeycloakCommonAutoConfiguration {
}
