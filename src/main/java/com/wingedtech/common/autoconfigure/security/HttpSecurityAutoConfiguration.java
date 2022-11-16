package com.wingedtech.common.autoconfigure.security;

import com.wingedtech.common.security.http.HttpSecurityConfiguration;
import com.wingedtech.common.security.http.HttpSecurityProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(value = HttpSecurityProperties.HTTP_SECURITY_CONFIG_ROOT + ".enabled", havingValue = "true", matchIfMissing = true)
@Import(HttpSecurityConfiguration.class)
public class HttpSecurityAutoConfiguration {
}
