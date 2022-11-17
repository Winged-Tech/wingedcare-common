package com.wingedtech.common.sensitivity.aop;

import com.wingedtech.common.sensitivity.SensitivityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;

/**
 * @author 6688SUN
 */
@Aspect
@Slf4j
public class DesensitizationAspect {

    @Pointcut("@annotation(com.wingedtech.common.sensitivity.annotation.SensitivityMark)")
    public void desensitizationPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }


    @AfterReturning(value = "desensitizationPointcut()", returning = "keys")
    public void doAfterReturningAdvice1(JoinPoint joinPoint, Object keys) {
        // 解析DesensitizationStrategy的字段进行对应策略脱敏
        try {
            if (SensitivityUtils.mustDesensitization()) {
                final Object body = ((ResponseEntity<?>) keys).getBody();
                SensitivityUtils.desensitizer(body);
            }
        } catch (Exception e) {
            log.error("Desensitizer failed: ", e);
        }
    }
}
