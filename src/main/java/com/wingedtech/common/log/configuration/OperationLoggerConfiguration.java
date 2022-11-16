package com.wingedtech.common.log.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wingedtech.common.log.LogConstant;
import com.wingedtech.common.log.service.OperationLogService;
import com.wingedtech.common.log.service.OperationLoggerService;
import com.wingedtech.common.log.service.impl.OperationLoggerServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = LogConstant.CONFIG_PREFIX + ".enabled")
public class OperationLoggerConfiguration {

    @Bean
    public OperationLoggerService operationLoggerService(OperationLogService operationLogService, ObjectMapper objectMapper) {
        return new OperationLoggerServiceImpl(operationLogService, objectMapper);
    }

}
