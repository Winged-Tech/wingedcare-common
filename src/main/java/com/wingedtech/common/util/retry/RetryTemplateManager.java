package com.wingedtech.common.util.retry;

import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Collections;
import java.util.Map;

public class RetryTemplateManager {

    public static RetryTemplate buildRetryTemplate(RetryTemplateConfig retryTemplateConfig) {
        Long retryPeriod = retryTemplateConfig != null ? retryTemplateConfig.getRetryPeriod() : null;
        Integer maxAttempts = retryTemplateConfig != null ? retryTemplateConfig.getMaxAttempts() : null;
        return buildRetryTemplate(retryPeriod, maxAttempts);
    }

    public static RetryTemplate buildRetryTemplate(Long retryPeriod, Integer maxAttempts) {
        RetryTemplate retryTemplate = new RetryTemplate();
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        RetryPolicy retryPolicy;
        if (retryPeriod != null) {
            backOffPolicy.setBackOffPeriod(retryPeriod);
        }
        if (maxAttempts != null) {
            if (maxAttempts == 0) {
                retryPolicy = new NeverRetryPolicy();
            } else {
                Map<Class<? extends Throwable>, Boolean> retryableExceptions = Collections.singletonMap(Exception.class, true);
                retryPolicy = new SimpleRetryPolicy(maxAttempts, retryableExceptions, true, true);
            }
        } else {
            retryPolicy = new SimpleRetryPolicy();
        }
        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);
        return retryTemplate;
    }
}
