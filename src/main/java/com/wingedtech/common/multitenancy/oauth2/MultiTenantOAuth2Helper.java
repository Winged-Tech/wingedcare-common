package com.wingedtech.common.multitenancy.oauth2;

import com.wingedtech.common.multitenancy.Constants;
import com.wingedtech.common.security.OAuth2SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Slf4j
public class MultiTenantOAuth2Helper {

    /**
     * 获取当前上下文用户认证信息中的tenant id
     *
     * @return
     */
    public static String getCurrentTenantId(TokenStore tokenStore) {
        OAuth2AccessToken oAuth2AccessToken = OAuth2SecurityUtils.getOAuth2AccessToken(tokenStore);
        if (oAuth2AccessToken != null) {
            return getTenantIdFromAccessToken(oAuth2AccessToken);
        } else {
            return null;
        }
    }

    public static String getTenantIdFromAccessToken(OAuth2AccessToken oAuth2AccessToken) {
        return OAuth2SecurityUtils.getTokenAdditionalInformation(oAuth2AccessToken, Constants.TOKEN_TENANT_INFORMATION_KEY);
    }

    public static String getCurrentTenantId() {
        return OAuth2SecurityUtils.getOAuth2AccessTokenValue(Constants.TOKEN_TENANT_INFORMATION_KEY);
    }
}
