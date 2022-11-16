package com.wingedtech.common.local.config;

import com.wingedtech.common.local.LocalConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 本地磁盘空间配置
 *
 * @author 6688SUN
 */
@Data
@ConfigurationProperties(value = LocalConstant.CONFIG_PREFIX)
public class DiskSpaceProperties {

    /**
     * 全局默认存储路径
     */
    private String globalStoragePath;

    /**
     * 是否开启事件通知 默认不开启
     */
    private Boolean enabledEvent;

    /**
     * 总的剩余空间低于多少告警 单位G
     */
    private Long lessThanOfSpaceAlarm;

    /**
     * 不同业务关联的一条完整数据占用空间 value 单位字节
     */
    private Map<String, Long> businessDataUseSpace = new HashMap<>(16);

}
