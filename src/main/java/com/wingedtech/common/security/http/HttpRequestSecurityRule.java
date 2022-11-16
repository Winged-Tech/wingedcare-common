package com.wingedtech.common.security.http;

import lombok.Data;
import org.springframework.http.HttpMethod;

@Data
public class HttpRequestSecurityRule {

    /**
     * 用于匹配请求路由的matcher规则
     */
    private String[] matchers;

    /**
     * 如果type为TEMPLATE类型，指定引用的模板名称
     */
    private String template;

    /**
     * HTTP method, 如果为null，则不限制method
     */
    private HttpMethod method;

    /**
     * 允许可访问的authorities
     */
    private String[] authorities;

    /**
     * 规则类型
     */
    private HttpRequestSecurityRuleType type = HttpRequestSecurityRuleType.ANT_MATCHER;

    /**
     * 验证模式
     */
    private HttpRequestAuthMode mode = HttpRequestAuthMode.HAS_ANY_AUTHORITIES;

    /**
     * 当时用ACCESS模式时，指定对应的表达式
     */
    private String expression;

    public enum HttpRequestAuthMode {
        /**
         * 使用permitAll
         */
        PERMIT_ALL,
        /**
         * 使用authenticated
         */
        AUTHENTICATED,
        /**
         * 使用hasAnyAuthorities
         */
        HAS_ANY_AUTHORITIES,
        /**
         * 使用access
         */
        ACCESS,
        /**
         * 使用denyAll
         */
        DENY_ALL,
        /**
         * 使用hasIpAddress
         */
        HAS_IP_ADDRESS
    }
}
