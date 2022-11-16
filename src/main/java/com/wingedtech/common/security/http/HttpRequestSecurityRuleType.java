package com.wingedtech.common.security.http;

public enum HttpRequestSecurityRuleType {
    /**
     * 加载一条预定义的模板
     */
    TEMPLATE,
    /**
     * ant matcher路由规则
     */
    ANT_MATCHER
}
