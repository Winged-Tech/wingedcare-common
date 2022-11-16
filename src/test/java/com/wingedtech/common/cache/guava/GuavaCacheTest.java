package com.wingedtech.common.cache.guava;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;

import java.util.concurrent.TimeUnit;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GuavaCacheTest {

	@Test
	public void testNameIsRequired() {
        Throwable exception = assertThrows(NullPointerException.class,  () -> {
            new GuavaCache(null);
        });
	}

	@Test
	public void testNewWithSpec() {
		CacheBuilderSpec spec = CacheBuilderSpec.parse("maximumSize=2");
		GuavaCache cache = new GuavaCache("name", spec, true);
		cache.put("key1", "value1");
		cache.put("key2", "value2");
		cache.put("key3", "value3");

		assertThat(cache.getNativeCache().size()).isEqualTo(2);
	}

	@Test
	public void testGet() {
		GuavaCache cache = new GuavaCache("name");
		cache.getNativeCache().put("key", "value");

		assertThat(cache.get("key").get()).isEqualTo("value");
	}

	@Test
	public void testGetAbsent() {
		GuavaCache cache = new GuavaCache("name");

		assertThat(cache.get("key")).isNull();
	}

	@Test
	public void testPut() {
		GuavaCache cache = new GuavaCache("name");
		cache.put("key", "value");

		assertThat(cache.getNativeCache().getIfPresent("key"))
				.isNotNull()
				.isEqualTo("value");
	}

	@Test
	public void testEvict() {
		GuavaCache cache = new GuavaCache("name");
		cache.getNativeCache().put("key", "value");

		assertThat(cache.getNativeCache().getIfPresent("key"))
				.isNotNull()
				.isEqualTo("value");

		cache.evict("key");
		assertThat(cache.getNativeCache().getIfPresent("key")).isNull();
	}

	@Test
	public void testClear() {
		GuavaCache cache = new GuavaCache("name");
		cache.getNativeCache().put("key1", "value1");
		cache.getNativeCache().put("key2", "value2");
		cache.getNativeCache().put("key3", "value3");

		assertThat(cache.getNativeCache().size()).isEqualTo(3);

		cache.clear();
		assertThat(cache.getNativeCache().size()).isZero();
	}

	@Test
	public void testAllowNullValues() {
		Cache cache = new GuavaCache("name", true);
		cache.put("key", null);

		assertThat(cache.get("key").get()).isNull();
	}

	@Test
	public void testDisallowNullValues() {

        Throwable exception = assertThrows(NullPointerException.class,  () -> {
            Cache cache = new GuavaCache("name", false);
            cache.put("key", null);
        });
	}

	@Test
	public void testExpire() {
		CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.SECONDS);
		GuavaCache cache = new GuavaCache("name", builder, false);
		cache.getNativeCache().put("key", "value");
		assertEquals("value", cache.get("key").get());

		// wait for expiration
		sleepUninterruptibly(3, TimeUnit.SECONDS);

		assertThat(cache.get("key")).isNull();
	}

}
