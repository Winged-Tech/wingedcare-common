package com.wingedtech.common.platform;


import com.wingedtech.common.constant.ClientType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * 用于标记业务功能类型所支持的客户端类型
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { TYPE, FIELD })
public @interface SupportingClientType {
    /**
     * 是否支持所有客户端
     */
    boolean allTypes() default false;
    /**
     * 支持的客户端类型列表
     * @return
     */
    ClientType[] value() default {};
}
