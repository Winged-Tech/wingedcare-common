package com.wingedtech.common.config;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * 用于ConfigProperties，标记该ConfigProperties对应的key
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { TYPE })
public @interface ConfigPropertiesKey {
    /**
     * 获取对应的key
     * @return
     */
    @NotNull String value();
}
