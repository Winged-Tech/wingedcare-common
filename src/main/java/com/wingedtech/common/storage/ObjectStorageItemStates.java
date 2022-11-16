package com.wingedtech.common.storage;

public enum ObjectStorageItemStates {
    /**
     * 对象已创建, 但未存储
     */
    CREATED,
    /**
     * 对象已存储
     */
    STORED,
    /**
     * 对象存储失败
     */
    FAILED
}
