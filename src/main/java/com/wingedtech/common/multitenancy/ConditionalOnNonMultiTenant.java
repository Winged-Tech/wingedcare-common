package com.wingedtech.common.multitenancy;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.*;

/**
 * @author taozhou
 * @date 2021/4/27
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ConditionalOnProperty(
    value = {Constants.CONFIG_MULTITENANCY_ENABLED},
    havingValue = "false",
    matchIfMissing = true
)
public @interface ConditionalOnNonMultiTenant {
}
