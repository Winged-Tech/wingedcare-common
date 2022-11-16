package com.wingedtech.common.autoconfigure.sensitivity;

import com.wingedtech.common.sensitivity.SensitivityConstant;
import com.wingedtech.common.sensitivity.aop.DesensitizationAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author 6688SUN
 */
@Configuration
@ConditionalOnProperty(value = SensitivityConstant.DESENSITIZATION + ".enabled", havingValue = "true")
@EnableAspectJAutoProxy
public class DesensitizationAutoConfiguration {

    @Bean
    DesensitizationAspect desensitizationAspect() {
        return new DesensitizationAspect();
    }
}
