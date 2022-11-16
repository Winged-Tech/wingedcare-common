package com.wingedtech.common.dto;

/**
 * 定义包含了敏感信息的数据类型
 */
public interface SensitiveData {
    /**
     * 对数据进行脱敏操作的通用方法
     */
    void desensitize();
}
