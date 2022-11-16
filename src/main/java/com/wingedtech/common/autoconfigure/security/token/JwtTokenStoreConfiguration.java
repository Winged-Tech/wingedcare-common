package com.wingedtech.common.autoconfigure.security.token;

import com.wingedtech.common.security.PlatformSecurityProperties;
import com.wingedtech.common.security.PlatformUserAuthenticationConverter;
import com.wingedtech.common.security.oauth2.OAuth2SignatureVerifierClient;
import com.wingedtech.common.security.oauth2.config.OAuth2JwtAccessTokenConverter;
import com.wingedtech.common.security.oauth2.config.OAuth2Properties;
import com.wingedtech.common.security.oauth2.token.ConditionalOnJwtTokenStore;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @author 6688 Sun
 */
@Configuration
@ConditionalOnMissingBean({AuthorizationServerEndpointsConfiguration.class})
@ConditionalOnJwtTokenStore
@AllArgsConstructor
public class JwtTokenStoreConfiguration {
    private final OAuth2Properties oAuth2Properties;
    private final PlatformSecurityProperties platformSecurityProperties;

    @Bean
    @Primary
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(OAuth2SignatureVerifierClient signatureVerifierClient) {
        OAuth2JwtAccessTokenConverter converter = new OAuth2JwtAccessTokenConverter(oAuth2Properties, signatureVerifierClient);
        if (platformSecurityProperties.getToken().isEnabledCompress()) {
            DefaultAccessTokenConverter defaultAccessTokenConverter = new DefaultAccessTokenConverter();
            defaultAccessTokenConverter.setUserTokenConverter(new PlatformUserAuthenticationConverter());
            converter.setAccessTokenConverter(defaultAccessTokenConverter);
        }
        return converter;
    }
}
