package com.wingedtech.common.security.http;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.wingedtech.common.constant.ConfigConstants;
import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(HttpSecurityProperties.HTTP_SECURITY_CONFIG_ROOT)
public class HttpSecurityProperties {
    public static final String HTTP_SECURITY_CONFIG_ROOT = ConfigConstants.WINGED_CONFIG_ROOT + "security.http";

    /**
     * 预定义的权限规则模板
     */
    private Map<String, List<HttpRequestSecurityRule>> templates;

    /**
     * 配置权限规则
     */
    private List<HttpRequestSecurityRule> rules;

    @JsonIgnore
    public boolean hasRules() {
        return !MoreObjects.firstNonNull(rules, ImmutableList.of()).isEmpty();
    }
}
