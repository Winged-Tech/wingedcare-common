package com.wingedtech.common.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.wingedtech.common.constant.ConfigConstants.WINGED_CONFIG_ROOT;

@ConfigurationProperties(value = WINGED_CONFIG_ROOT + "feign")
@Data
public class FeignClientProperties {
    /**
     * OAuth2InterceptedFeignConfiguration 是否要自动预热获取token
     * 如果不指定，则默认为打开状态
     */
    private Boolean autoWarmUp;
}
