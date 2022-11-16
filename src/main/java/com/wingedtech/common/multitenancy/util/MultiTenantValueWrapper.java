package com.wingedtech.common.multitenancy.util;

import com.wingedtech.common.multitenancy.Tenant;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 用于对指定的值类型进行包装，以达到对于不同的租户有不同的值的效果
 * 可在非多租户模式下使用。
 * @param <T>
 */
public class MultiTenantValueWrapper<T> {
    private T defaultValue = null;
    private ConcurrentHashMap<String, T> map = new ConcurrentHashMap<>();

    public T getDefault() {
        return defaultValue;
    }

    public void setDefault(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isPresent() {
        if (Tenant.isEnabledMultitenancy()) {
            return map.containsKey(Tenant.getCurrentTenantIdOrMaster());
        }
        else {
            return this.defaultValue != null;
        }
    }

    public T get() {
        if (Tenant.isEnabledMultitenancy()) {
            return map.get(Tenant.getCurrentTenantIdOrMaster());
        }
        else {
            return getDefault();
        }
    }

    public synchronized void set(T value) {
        if (Tenant.isEnabledMultitenancy()) {
            map.put(Tenant.getCurrentTenantIdOrMaster(), value);
        }
        else {
            setDefault(value);
        }
    }

    public synchronized T getOrCalculate(Supplier<T> supplier) {
        if (!isPresent()) {
            set(supplier.get());
        }
        return get();
    }

    /**
     * 对wrapper内的每一个租户apply指定的consumer
     * @param consumer
     */
    public void forEachTenant(Consumer<T> consumer) {
        map.forEach((tenant, value) -> consumer.accept(value));
    }
}
