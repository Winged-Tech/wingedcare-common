package com.wingedtech.common.storage.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.wingedtech.common.storage.config.ObjectStorageResourceConfigProperties.WINGED_OSS_ROOT;

/**
 * 动态刷新服务配置
 * 必须在需要刷新的服务配置management.endpoints.web.exposure.include=refresh
 *
 * @author 6688SUN
 */
@RestController
@RequestMapping("/api/internal/winged")
@Slf4j
public class DynamicConfigurationInternalResource {

    public static final String OSS_FILE_ROOT_KEY = WINGED_OSS_ROOT + ".file.root";
    @Autowired
    private ContextRefresher contextRefresher;
    @Autowired
    private ConfigurableEnvironment env;

    /**
     * 当前服务名
     */
    @Value("${spring.application.name}")
    private String serviceName;

    /**
     * 动态刷新数据存放路径配置
     *
     * @param dataStorePath 数据存放路径
     */
    @GetMapping("/oss-file-root/refresh-*")
    public void refresh(@RequestParam String dataStorePath) {
        log.info("Refresh the storage [{}: {}] configuration of the {} service.", OSS_FILE_ROOT_KEY, dataStorePath, serviceName);
        // 修改配置文件中属性
        Map<String, Object> map = new HashMap<>(1);
        map.put(OSS_FILE_ROOT_KEY, dataStorePath);
        MapPropertySource propertySource = new MapPropertySource("ossFileRootRefresh", map);
        // 将修改后的配置设置到environment中
        env.getPropertySources().addFirst(propertySource);
        contextRefresher.refresh();
        log.info("Refresh the storage [{}: {}] configuration of the {} service success.", OSS_FILE_ROOT_KEY, dataStorePath, serviceName);
    }
}
