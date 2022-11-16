package com.wingedtech.common.security.http;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class HttpRequestSecurityTemplate {
    /**
     * 模板名称
     */
    @NotBlank
    private String name;

    /**
     * 模板内定义的规则
     */
    private List<HttpRequestSecurityRule> rules;
}
