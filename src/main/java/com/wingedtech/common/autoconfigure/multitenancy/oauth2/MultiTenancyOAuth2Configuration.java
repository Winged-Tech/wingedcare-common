package com.wingedtech.common.autoconfigure.multitenancy.oauth2;

import com.wingedtech.common.autoconfigure.multitenancy.oauth2.authorization.MultiTenancyAuthorizationServerSecurityConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MultiTenancyAuthorizationServerSecurityConfiguration.class, MultiTenancyWebSecurityConfiguration.class})
public class MultiTenancyOAuth2Configuration {
}
