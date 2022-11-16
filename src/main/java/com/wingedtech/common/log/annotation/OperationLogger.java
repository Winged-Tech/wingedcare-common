package com.wingedtech.common.log.annotation;

import java.lang.annotation.*;

/**
 * @author apple
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLogger {

    /**
     * 请求执行的方法描述
     */
    String description() default "";

    /**
     * 是否记录返回值 默认不记录
     */
    boolean ignoreResponse() default true;
}
