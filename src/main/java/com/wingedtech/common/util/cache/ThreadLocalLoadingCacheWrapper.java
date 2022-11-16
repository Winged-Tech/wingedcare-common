package com.wingedtech.common.util.cache;

import com.google.common.base.Function;
import com.google.common.cache.*;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.ExecutionException;

/**
 * 一个Cache生命周期在当前Thread上下文内有效的LoadingCache实现
 * @author taozhou
 * @date 2021/1/18
 */
@Slf4j
public class ThreadLocalLoadingCacheWrapper<K, V> extends AbstractLoadingCache<K, V> implements LoadingCache<K, V> {
    public static final int DEFAULT_MAX_SIZE = 10;
    private ThreadLocal<LoadingCache<K, V>> cache;

    public ThreadLocalLoadingCacheWrapper(CacheLoader<K, V> loader) {
        cache = ThreadLocal.withInitial(() -> CacheBuilder.newBuilder().maximumSize(DEFAULT_MAX_SIZE).build(loader));
    }

    /**
     * 使用一个function创建一个ThreadLocalLoadingCacheWrapper
     * @param function
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> ThreadLocalLoadingCacheWrapper<K, V> from(Function<K, V> function) {
        return new ThreadLocalLoadingCacheWrapper(CacheLoader.from(function));
    }

    protected LoadingCache<K, V> getInternalCache() {
        return cache.get();
    }

    @Override
    public V get(K key) throws ExecutionException {
        return getInternalCache().get(key);
    }

    @Override
    public @Nullable V getIfPresent(Object key) {
        return getInternalCache().getIfPresent(key);
    }

    @Override
    public void invalidate(Object key) {
        getInternalCache().invalidate(key);
    }

    @Override
    public void invalidateAll(Iterable<?> keys) {
        getInternalCache().invalidateAll(keys);
    }

    @Override
    public void invalidateAll() {
        getInternalCache().invalidateAll();
    }

    @Override
    public CacheStats stats() {
        return getInternalCache().stats();
    }

    /**
     * 删除内部的ThreadLocal缓存
     */
    public void remove() {
        cache.remove();
    }
}
