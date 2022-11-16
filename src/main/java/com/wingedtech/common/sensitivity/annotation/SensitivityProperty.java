package com.wingedtech.common.sensitivity.annotation;

import com.wingedtech.common.sensitivity.DefaultSensitivityCustomStrategy;
import com.wingedtech.common.sensitivity.SensitivityCustomStrategy;
import com.wingedtech.common.sensitivity.SensitivityStrategy;

import java.lang.annotation.*;

/**
 * 脱敏注解
 *
 * @author 6688SUN
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.FIELD})
@Inherited
public @interface SensitivityProperty {

    boolean useCustom() default false;

    /**
     * 自定义脱敏策略
     */
    Class<? extends SensitivityCustomStrategy> custom() default DefaultSensitivityCustomStrategy.class;

    /**
     * 已有脱敏策略
     */
    SensitivityStrategy strategy() default SensitivityStrategy.USERNAME;
}
