package com.wingedtech.common.cache.guava;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Uninterruptibles;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.extractProperty;

public class GuavaCacheManagerTest {

	@Test
	public void testGetNamesStaticCaches() {
		Collection<GuavaCache> caches = ImmutableList.of(
				new GuavaCache("cache1"),
				new GuavaCache("cache2"),
				new GuavaCache("cache3")
		);

		GuavaCacheManager manager = new GuavaCacheManager();
		manager.setCaches(caches);
		manager.afterPropertiesSet();

		Collection<String> cacheNames = manager.getCacheNames();
		assertThat(caches).hasSameSizeAs(cacheNames);
		assertThat(extractProperty("name").from(caches))
				.containsAll(cacheNames);
	}

	@Test
	public void testGetCacheStaticCaches() {
		Collection<GuavaCache> caches = ImmutableList.of(
				new GuavaCache("cache1"),
				new GuavaCache("cache2"),
				new GuavaCache("cache3")
		);

		GuavaCacheManager manager = new GuavaCacheManager();
		manager.setCaches(caches);
		manager.afterPropertiesSet();

		for (GuavaCache cache : caches) {
			assertThat(manager.getCache(cache.getName())).isSameAs(cache);
		}
	}

	@Test
	public void testGetNamesDynamicCaches() {
		GuavaCacheManager manager = new GuavaCacheManager();
		manager.afterPropertiesSet();

		// no cache available by default
		assertThat(manager.getCacheNames()).isEmpty();

		// getting a new cache will add-it to available caches
		assertThat(manager.getCache("cache1").getName()).isEqualTo("cache1");
		assertThat(manager.getCacheNames()).hasSize(1);
		assertThat(manager.getCache("cache2").getName()).isEqualTo("cache2");
		assertThat(manager.getCacheNames()).hasSize(2);

		// get existing cache
		assertThat(manager.getCache("cache1").getName()).isEqualTo("cache1");
		assertThat(manager.getCacheNames()).hasSize(2);
	}

	@Test
	public void testGetCacheDynamicCachesDefaultConfig() {
		GuavaCacheManager manager = new GuavaCacheManager();
		manager.afterPropertiesSet();

		GuavaCache cache = (GuavaCache) manager.getCache("cache1");
		assertThat(cache.isAllowNullValues()).isTrue();
	}

	@Test
	public void testGetCacheDynamicCachesCustomConfig() {
		GuavaCacheManager manager = new GuavaCacheManager();
		manager.setAllowNullValues(false);
		manager.setSpec("maximumSize=2,expireAfterWrite=2s");
		manager.afterPropertiesSet();

		GuavaCache cache = (GuavaCache) manager.getCache("cache1");
		assertThat(cache.isAllowNullValues()).isFalse();
		cache.put("key1", "value1");
		cache.put("key2", "value2");
		cache.put("key3", "value3");
		assertThat(cache.getNativeCache().size()).isEqualTo(2);

		Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
		// evict stale entries
		cache.getNativeCache().cleanUp();
		assertThat(cache.getNativeCache().size()).isZero();
	}

}
