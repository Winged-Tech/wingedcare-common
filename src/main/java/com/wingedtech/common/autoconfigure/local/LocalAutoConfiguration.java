package com.wingedtech.common.autoconfigure.local;

import com.wingedtech.common.local.LocalConstant;
import com.wingedtech.common.local.config.DiskSpaceProperties;
import com.wingedtech.common.local.service.DiskSpaceService;
import com.wingedtech.common.local.service.impl.DiskSpaceServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 本地主机环境自动配置
 *
 * @author 6688SUN
 */
@Configuration
@ConditionalOnProperty(value = LocalConstant.CONFIG_PREFIX + ".enabled", havingValue = "true")
@EnableConfigurationProperties(DiskSpaceProperties.class)
public class LocalAutoConfiguration {

    @Bean
    public DiskSpaceService diskSpaceService(DiskSpaceProperties diskSpaceProperties) {
        return new DiskSpaceServiceImpl(diskSpaceProperties);
    }
}
