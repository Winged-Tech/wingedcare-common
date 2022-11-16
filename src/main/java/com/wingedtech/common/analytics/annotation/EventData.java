package com.wingedtech.common.analytics.annotation;

import java.lang.annotation.*;

/**
 * @author apple
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EventData {

    /**
     * 事件名称
     */
    String name() default "";
}
