package com.wingedtech.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(Constants.CONFIG_SERVICE_PROPERTIES_ROOT)
public class ConfigServiceProperties {
    private String provider = "resources";
}
