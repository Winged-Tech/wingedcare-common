package com.wingedtech.common.multitenancy;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.*;

/**
 * @author taozhou
 * @date 2020/10/9
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ConditionalOnProperty(
    value = {Constants.CONFIG_MULTITENANCY_ENABLED}, havingValue = "true"
)
public @interface ConditionalOnMultiTenant {
}
