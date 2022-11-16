package com.wingedtech.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * A registry used to manage multiple beans of the same interface by keys.
 * @param <K> Unique key to identify each of the beans.
 * @param <B> Interface of the beans.
 */
@Slf4j
public class InMemoryBeanRegistry<K, B extends RegistrableBean<K>> {
    private Map<K, B> map = new HashMap<>();

    public InMemoryBeanRegistry() {
    }

    /**
     * 注册一个bean
     * @param bean
     */
    public void register(B bean) {
        K key = bean.getKey();
        if (key == null) {
            throw new IllegalArgumentException("The key of RegistrableBean cannot be null!");
        }
        if (map.containsKey(key)) {
            throw new IllegalStateException("The key has already been registered: " + key);
        }
        map.put(bean.getKey(), bean);
        log.info("Registered bean of type {} with key {}", bean.getClass().getName(), key);
    }

    /**
     * 从已注册的bean中获取指定key的bean
     * @param key
     * @return
     */
    public B getBean(K key) {
        return map.get(key);
    }
}
