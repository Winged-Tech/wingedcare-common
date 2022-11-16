package com.wingedtech.common.multitenancy.oauth2.authorization;

import com.wingedtech.common.multitenancy.Constants;
import com.wingedtech.common.multitenancy.Tenant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

public class TenantTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        if (Tenant.isEnabledMultitenancy()) {
            addClaims((DefaultOAuth2AccessToken) oAuth2AccessToken);
        }
        return oAuth2AccessToken;
    }

    private void addClaims(DefaultOAuth2AccessToken accessToken) {
        DefaultOAuth2AccessToken token = accessToken;
        Map<String, Object> additionalInformation = token.getAdditionalInformation();
        if (additionalInformation.isEmpty()) {
            additionalInformation = new LinkedHashMap<>();
        }
        // add "tenant id" claim
        // 严格确保tenant的正确信息
        if (!Tenant.isCurrentTenantIdSet()) {
            throw new IllegalStateException("Current tenant is null!");
        }
        additionalInformation.put(Constants.TOKEN_TENANT_INFORMATION_KEY, Tenant.getCurrentTenantId());
        token.setAdditionalInformation(additionalInformation);
    }
}
