package com.wingedtech.common.streams.check;

import com.wingedtech.common.constant.ConfigConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = CheckingStreamProperties.CONFIG_ROOT)
public class CheckingStreamProperties {
    public static final String CONFIG_ROOT = ConfigConstants.WINGED_CONFIG_ROOT + "checking.streams";
    /**
     * 是否开启此功能
     */
    private boolean enabled = true;

    /**
     * 当服务启动后, 是否自动上报一条消息
     */
    private boolean autoReportOnStartup = false;
}
