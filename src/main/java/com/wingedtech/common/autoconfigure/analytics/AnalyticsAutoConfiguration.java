package com.wingedtech.common.autoconfigure.analytics;

import com.wingedtech.common.analytics.AnalyticsConstant;
import com.wingedtech.common.analytics.aop.EventDataAspect;
import com.wingedtech.common.analytics.configuration.AnalyticesConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * @author apple
 */
@Configuration
@ConditionalOnProperty(value = AnalyticsConstant.CONFIG_PREFIX + ".enabled")
@Import(AnalyticesConfiguration.class)
@EnableAspectJAutoProxy
public class AnalyticsAutoConfiguration {

    @Bean
    public EventDataAspect eventDataAspect() {
        return new EventDataAspect();
    }
}
