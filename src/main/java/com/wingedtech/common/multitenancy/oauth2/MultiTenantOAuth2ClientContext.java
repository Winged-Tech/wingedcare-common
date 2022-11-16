package com.wingedtech.common.multitenancy.oauth2;

import com.wingedtech.common.multitenancy.util.MultiTenantValueWrapper;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MultiTenantOAuth2ClientContext implements OAuth2ClientContext, Serializable {

    private static final long serialVersionUID = 914967629530462926L;

    private MultiTenantValueWrapper<OAuth2AccessToken> accessToken = new MultiTenantValueWrapper<>();

    private MultiTenantValueWrapper<AccessTokenRequest> accessTokenRequest = new MultiTenantValueWrapper<>();

    private Map<String, Object> state = new HashMap<String, Object>();

    public MultiTenantOAuth2ClientContext() {
        this.accessTokenRequest.setDefault(createAccessTokenRequest());
    }

    public MultiTenantOAuth2ClientContext(AccessTokenRequest accessTokenRequest) {
        this.accessTokenRequest.setDefault(accessTokenRequest);
    }

    public MultiTenantOAuth2ClientContext(OAuth2AccessToken accessToken) {
        this.accessToken.setDefault(accessToken);
        this.accessTokenRequest.setDefault(createAccessTokenRequest());
    }

    @Override
    public OAuth2AccessToken getAccessToken() {
        return accessToken.get();
    }

    @Override
    public void setAccessToken(OAuth2AccessToken accessToken) {
        this.accessToken.set(accessToken);
        this.accessTokenRequest.get().setExistingToken(accessToken);
    }

    @Override
    public AccessTokenRequest getAccessTokenRequest() {
        AccessTokenRequest accessTokenRequest = this.accessTokenRequest.get();
        if (accessTokenRequest == null) {
            accessTokenRequest = createAccessTokenRequest();
            this.accessTokenRequest.set(accessTokenRequest);
        }
        return accessTokenRequest;
    }

    @Override
    public void setPreservedState(String stateKey, Object preservedState) {
        state.put(stateKey, preservedState);
    }

    @Override
    public Object removePreservedState(String stateKey) {
        return state.remove(stateKey);
    }

    private DefaultAccessTokenRequest createAccessTokenRequest() {
        return new DefaultAccessTokenRequest();
    }
}
