package com.wingedtech.common.filter.content;

import org.springframework.util.Assert;

public class BusinessSourceContextHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void clearContext() {
        contextHolder.remove();
    }

    public static String getContext() {
        return contextHolder.get();
    }

    public static void setContext(String context) {
        Assert.notNull(context, "Only non-null BusinessSourceContext instances are permitted");
        contextHolder.set(context);
    }

}
