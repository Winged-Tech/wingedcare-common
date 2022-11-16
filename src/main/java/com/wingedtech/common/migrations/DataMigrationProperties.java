package com.wingedtech.common.migrations;

import com.wingedtech.common.constant.ConfigConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

import static com.wingedtech.common.migrations.Constants.CONFIG_ROOT;

@Data
@ConfigurationProperties(CONFIG_ROOT)
public class DataMigrationProperties {
    /**
     * 是否自动注入migrations相关服务，默认关闭
     */
    private boolean enabled = false;
    /**
     * 针对各种DataProcessor的配置
     */
    private Map<String, DataProcessorProperties> processors;

    public DataProcessorProperties findProcessorConfig(String name) {
        if (processors != null && processors.containsKey(name)) {
            return processors.get(name);
        }
        return DataProcessorProperties.DEFAULT;
    }
}
