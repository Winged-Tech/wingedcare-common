package com.wingedtech.common.security.http;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import java.util.List;
import java.util.Map;

@Slf4j
public class HttpSecurityConfigurer {
    public static ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry, List<HttpRequestSecurityRule> rules, Map<String, List<HttpRequestSecurityRule>> templates) {
        for (HttpRequestSecurityRule rule : rules) {
            registry = configure(registry, rule, templates);
        }
        return registry;
    }

    public static ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry, HttpRequestSecurityRule rule, Map<String, List<HttpRequestSecurityRule>> templates) {
        if (rule.getType() == HttpRequestSecurityRuleType.TEMPLATE) {
            final String templateName = rule.getTemplate();
            Preconditions.checkNotNull(templateName, "HttpRequestSecurityRule 未指定模板名称：{}", rule);
            List<HttpRequestSecurityRule> template = templates.get(templateName);
            Preconditions.checkNotNull(template, "找不到模板" + templateName);
            return configure(registry, template, templates);
        } else {
            return configure(registry, rule);
        }
    }

    public static ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry, HttpRequestSecurityRule rule) {
        final HttpRequestSecurityRule.HttpRequestAuthMode mode = rule.getMode();

        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl matchers;

        if (rule.getMethod() != null) {
            matchers = registry.antMatchers(rule.getMethod(), rule.getMatchers());
        } else {
            matchers = registry.antMatchers(rule.getMatchers());
        }

        switch (mode) {
            case PERMIT_ALL:
                return matchers.permitAll();
            case HAS_ANY_AUTHORITIES:
                return matchers.hasAnyAuthority(rule.getAuthorities());
            case AUTHENTICATED:
                return matchers.authenticated();
            case ACCESS:
                return matchers.access(rule.getExpression());
            case DENY_ALL:
                return matchers.denyAll();
            case HAS_IP_ADDRESS:
                return matchers.hasIpAddress(rule.getExpression());
            default:
                log.error("We're not supposed to reach here.");
                return null;
        }
    }
}
