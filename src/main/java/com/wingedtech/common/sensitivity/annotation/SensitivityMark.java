package com.wingedtech.common.sensitivity.annotation;

import java.lang.annotation.*;

/**
 * 脱敏标记
 *
 * @author 6688SUN
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Inherited
public @interface SensitivityMark {
}
