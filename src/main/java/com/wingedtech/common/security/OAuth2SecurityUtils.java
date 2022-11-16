package com.wingedtech.common.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.Map;

@Slf4j
public class OAuth2SecurityUtils {

    private static JsonParser jsonParser = JsonParserFactory.getJsonParser();

    /**
     * 从OAuth2AccessToken中解析出指定的additional information
     */
    public static String getTokenAdditionalInformation(OAuth2AccessToken oAuth2AccessToken, String key) {
        Map<String, Object> additionalInformation = oAuth2AccessToken.getAdditionalInformation();
        Object value = additionalInformation.get(key);
        return value == null ? null : (String) value;
    }

    /**
     * 获取当前上下文用户认证信息中的OAuth2AccessToken
     */
    public static OAuth2AccessToken getOAuth2AccessToken(TokenStore tokenStore) {
        final String tokenValue = getTokenValue();
        if (tokenValue != null) {
            return tokenStore.readAccessToken(tokenValue);
        }
        return null;
    }

    private static String getTokenValue() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null && securityContext.getAuthentication() != null) {
            Object details = securityContext.getAuthentication().getDetails();
            if (details == null) {
                return null;
            }
            if (details instanceof OAuth2AuthenticationDetails) {
                OAuth2AuthenticationDetails authenticationDetails = (OAuth2AuthenticationDetails) details;
                return authenticationDetails.getTokenValue();
            }
        }
        return null;
    }

    public static String getOAuth2AccessTokenValue(String tokenKey) {
        final String tokenValue = getTokenValue();
        if (tokenValue != null) {
            if (isJwtToken(tokenValue)) {
                return getClaim(tokenValue, tokenKey, String.class);
            } else {
                throw new UnsupportedOperationException("非JWT token无法直接解析tokenKey");
            }
        }
        return null;
    }

    /**
     * 判断指定的tokenValue是否是JWT token
     */
    public static boolean isJwtToken(String tokenValue) {
        int firstPeriod = tokenValue.indexOf('.');
        int lastPeriod = tokenValue.lastIndexOf('.');
        return firstPeriod > 0 && lastPeriod > firstPeriod;
    }

    /**
     * Retrieve the given claim from the given token.
     *
     * @param accessToken the JWT token to examine.
     * @param claimName   name of the claim to get.
     * @param clazz       the Class we expect to find there.
     * @return the desired claim.
     * @throws InvalidTokenException if we cannot find the claim in the token or it is of wrong type.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getClaim(String accessToken, String claimName, Class<T> clazz) {
        Jwt jwt = JwtHelper.decode(accessToken);
        String claims = jwt.getClaims();
        Map<String, Object> claimsMap = jsonParser.parseMap(claims);
        Object claimValue = claimsMap.get(claimName);
        if (claimValue == null) {
            return null;
        }
        if (!clazz.isAssignableFrom(claimValue.getClass())) {
            throw new InvalidTokenException("claim is not of expected type: " + claimName);
        }
        return (T) claimValue;
    }
}
