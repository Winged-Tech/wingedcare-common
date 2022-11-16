package com.wingedtech.common.multitenancy.oauth2;

import com.wingedtech.common.multitenancy.Constants;
import com.wingedtech.common.multitenancy.Tenant;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.OAuth2AccessTokenSupport;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Arrays;
import java.util.List;

/**
 * 多租户配置下的OAuth2FeignRequestInterceptor
 */
@Slf4j
public class MultiTenantOAuth2FeignRequestInterceptor extends OAuth2FeignRequestInterceptor {

    /**
     * 是否自动进行token warm up，缺省状态开启（uaa服务建议关闭此功能）
     */
    private final Boolean autoWarmUp;

    public MultiTenantOAuth2FeignRequestInterceptor(OAuth2ClientContext oAuth2ClientContext, OAuth2ProtectedResourceDetails resource, Boolean autoWarmUp) {
        super(oAuth2ClientContext, resource);
        this.autoWarmUp = autoWarmUp;

        super.setAccessTokenProvider(buildDefaultTokenProviderChain());

        this.tokenWarmUp();
    }

    public MultiTenantOAuth2FeignRequestInterceptor(OAuth2ClientContext oAuth2ClientContext, OAuth2ProtectedResourceDetails resource, String tokenType, String header, Boolean autoWarmUp) {
        super(oAuth2ClientContext, resource, tokenType, header);
        this.autoWarmUp = autoWarmUp;

        super.setAccessTokenProvider(buildDefaultTokenProviderChain());

        this.tokenWarmUp();
    }

    @Override
    public void apply(RequestTemplate template) {
        super.apply(template);
        template.header(Constants.HEADER_TENANT_ID, Tenant.getCurrentTenantId());
    }

    @Override
    public void setAccessTokenProvider(AccessTokenProvider tokenProvider) {
        super.setAccessTokenProvider(applyRequestInterceptor(tokenProvider));
    }

    /**
     * 在启动时进行热身，预先获取token，防止启动后第一次请求的缓慢
     */
    private void tokenWarmUp() {
        if (BooleanUtils.isNotFalse(autoWarmUp)) {
            log.info("Warming up access token");
            try {
                final OAuth2AccessToken token = super.getToken();
                if (token != null) {
                    log.info("Access token is ready!");
                } else {
                    log.warn("Cannot get access token");
                }
            } catch (Exception e) {
                log.error("Unable to get access token: {}", e.getMessage());
            }
        }
        else {
            log.info("Skip access token auto warm up");
        }
    }

    public AccessTokenProvider buildDefaultTokenProviderChain() {
        return new AccessTokenProviderChain(Arrays
                .asList(applyRequestInterceptor(new AuthorizationCodeAccessTokenProvider()),
                        applyRequestInterceptor(new ImplicitAccessTokenProvider()),
                        applyRequestInterceptor(new ResourceOwnerPasswordAccessTokenProvider()),
                        applyRequestInterceptor(new ClientCredentialsAccessTokenProvider())));
    }

    public AccessTokenProvider applyRequestInterceptor(AccessTokenProvider tokenProvider) {
        // 给OAuth2AccessTokenSupport注入多租户header的RequestInterceptor
        if (tokenProvider instanceof OAuth2AccessTokenSupport) {
            OAuth2AccessTokenSupport tokenSupport = (OAuth2AccessTokenSupport) tokenProvider;
            List<ClientHttpRequestInterceptor> interceptors = Arrays.asList(new MultiTenantHttpRequestInterceptor());
            tokenSupport.setInterceptors(interceptors);
        }
        return tokenProvider;
    }
}
