package com.wingedtech.common.storage;

/**
 * 对象存储类型
 * @author taozhou
 */
public enum ObjectStorageType {
    /**
     * 供公开访问的资源
     */
    PUBLIC_RESOURCE,
    /**
     * 私有访问的资源，访问前需对url进行签名
     */
    PRIVATE_RESOURCE
}
