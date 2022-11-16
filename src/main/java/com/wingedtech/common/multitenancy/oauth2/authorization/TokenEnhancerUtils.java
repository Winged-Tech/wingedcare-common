package com.wingedtech.common.multitenancy.oauth2.authorization;

import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.List;

public class TokenEnhancerUtils {
    /**
     * 确保指定的tokenEnhancers内包含TenantTokenEnhancer
     * @param tokenEnhancers
     */
    public static List<TokenEnhancer> checkTenantTokenEnhancer(List<TokenEnhancer> tokenEnhancers) {
        boolean foundTenantTokenEnhancer = false;
        TokenEnhancer tenantTokenEnhancer = null;
        for (TokenEnhancer tokenEnhancer : tokenEnhancers) {
            if (tokenEnhancer instanceof TenantTokenEnhancer) {
                foundTenantTokenEnhancer = true;
                tenantTokenEnhancer = tokenEnhancer;
                break;
            }
        }

        if (foundTenantTokenEnhancer) {
            tokenEnhancers.remove(tenantTokenEnhancer);
        }
        else {
            tenantTokenEnhancer = new TenantTokenEnhancer();
        }

        // 必须确保TenantTokenEnhancer在JwtAccessTokenConverter之前，否则最终的Jwt token不会包含额外信息
        tokenEnhancers.add(0, tenantTokenEnhancer);
        return tokenEnhancers;
    }
}
