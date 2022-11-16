package com.wingedtech.common.multitenancy.config;

import lombok.Data;

import java.util.List;

@Data
public class MultiTenancyFilterProperties {
    /**
     * MultiTenancyFilter应该完全忽略的请求，均使用antPathMatcher格式
     */
    private List<String> excludes;

    /**
     * 不应该进行token解析的请求，均使用antPathMatcher格式
     */
    private List<String> ignoresToken;
}