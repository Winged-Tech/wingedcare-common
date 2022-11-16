package com.wingedtech.common.autoconfigure.log;

import com.wingedtech.common.log.LogConstant;
import com.wingedtech.common.log.aop.OperationLogAspect;
import com.wingedtech.common.log.configuration.OperationLoggerConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * @author apple
 */
@Configuration
@ConditionalOnProperty(value = LogConstant.CONFIG_PREFIX + ".enabled")
@Import(OperationLoggerConfiguration.class)
@EnableAspectJAutoProxy
public class OperationLogAutoConfiguration {

    @Bean
    public OperationLogAspect loggingAspect() {
        return new OperationLogAspect();
    }
}
