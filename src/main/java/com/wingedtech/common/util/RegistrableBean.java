package com.wingedtech.common.util;

public interface RegistrableBean<K> {
    /**
     * 获取一个用于唯一标示该bean的key
     * @return
     */
    K getKey();
}


