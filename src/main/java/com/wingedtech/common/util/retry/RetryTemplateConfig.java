package com.wingedtech.common.util.retry;

import lombok.Data;

@Data
public class RetryTemplateConfig {

    /**
     * 重试时间间隔
     */
    private Long retryPeriod;

    /**
     * 最大重试次数
     */
    private Integer maxAttempts;
}
