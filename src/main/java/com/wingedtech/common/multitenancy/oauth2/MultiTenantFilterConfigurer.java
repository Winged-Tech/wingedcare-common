package com.wingedtech.common.multitenancy.oauth2;

import com.wingedtech.common.multitenancy.MultiTenantFilter;
import com.wingedtech.common.multitenancy.config.MultiTenancyFilterProperties;
import com.wingedtech.common.multitenancy.config.MultiTenancyProperties;
import com.wingedtech.common.multitenancy.filter.MultiTenantPostAuthFilter;
import com.wingedtech.common.multitenancy.filter.MultiTenantPreAuthFilter;
import com.wingedtech.common.security.oauth2.token.TokenStores;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MultiTenantFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private MultiTenancyProperties multiTenancyProperties;
    private String tokenStore;

    public MultiTenantFilterConfigurer(MultiTenancyProperties multiTenancyProperties, String tokenStore) {
        this.multiTenancyProperties = multiTenancyProperties;
        this.tokenStore = tokenStore;
    }

    /**
     * Install RefreshTokenFilter as a servlet Filter.
     */
    @Override
    public void configure(HttpSecurity http) {
        final RequestMatcher excludingRequestMatcher = buildExcludingRequestMatcher(multiTenancyProperties.getFilter());
        final RequestMatcher ignoringTokenRequestMatcher = buildIgnoringTokenRequestMatcher(multiTenancyProperties.getFilter());
        // 对于MongoDB或者JDBC等需要连接数据的tokenStore, 采用特殊的两段式filter
        if (TokenStores.isMongoDBTokenStore(tokenStore) || TokenStores.isJdbcTokenStore(tokenStore)) {
            MultiTenantPostAuthFilter postAuthFilter = new MultiTenantPostAuthFilter(excludingRequestMatcher, ignoringTokenRequestMatcher);
            MultiTenantPreAuthFilter preAuthFilter = new MultiTenantPreAuthFilter(multiTenancyProperties, excludingRequestMatcher);
            http.addFilterBefore(preAuthFilter, OAuth2AuthenticationProcessingFilter.class);
            http.addFilterAfter(postAuthFilter, OAuth2AuthenticationProcessingFilter.class);
        } else {
            MultiTenantFilter customFilter = new MultiTenantFilter(multiTenancyProperties, excludingRequestMatcher, ignoringTokenRequestMatcher);
            http.addFilterAfter(customFilter, OAuth2AuthenticationProcessingFilter.class);
        }
        log.info("Configured MultiTenantFilter for token store: {}.", tokenStore);
    }

    /**
     * 构造一个RequestMatcher，用于筛选出不解析token 多租户信息的请求
     */
    public static RequestMatcher buildIgnoringTokenRequestMatcher(MultiTenancyFilterProperties filterProperties) {
        if (filterProperties == null || CollectionUtils.isEmpty(filterProperties.getIgnoresToken())) {
            return null;
        }
        List<RequestMatcher> matchers = new ArrayList<>();
        for (String filter : filterProperties.getIgnoresToken()) {
            matchers.add(new AntPathRequestMatcher(filter));
        }
        return new OrRequestMatcher(matchers);
    }

    public static RequestMatcher buildExcludingRequestMatcher(MultiTenancyFilterProperties filterProperties) {
        if (filterProperties == null || CollectionUtils.isEmpty(filterProperties.getExcludes())) {
            return null;
        }
        List<RequestMatcher> matchers = new ArrayList<>();
        // 默认添加所有的静态资源路径
        matchers.add(PathRequest.toStaticResources().atCommonLocations());
        for (String filter : filterProperties.getExcludes()) {
            matchers.add(new AntPathRequestMatcher(filter));
        }
        return new OrRequestMatcher(matchers);
    }
}
