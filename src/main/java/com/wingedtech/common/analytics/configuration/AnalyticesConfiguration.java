package com.wingedtech.common.analytics.configuration;

import com.wingedtech.common.analytics.AnalyticsConstant;
import com.wingedtech.common.analytics.service.EventDataService;
import com.wingedtech.common.analytics.service.IEventDataService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = AnalyticsConstant.CONFIG_PREFIX + ".enabled")
public class AnalyticesConfiguration {

    @Bean
    public IEventDataService eventDataService(EventDataService eventDataService) {
        return new IEventDataService(eventDataService);
    }

}
