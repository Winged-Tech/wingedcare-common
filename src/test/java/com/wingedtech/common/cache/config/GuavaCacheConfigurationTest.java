package com.wingedtech.common.cache.config;

import com.wingedtech.common.autoconfigure.multitenancy.cache.GuavaCacheAutoConfiguration;
import com.wingedtech.common.autoconfigure.multitenancy.cache.MultiTenantCacheAutoConfiguration;
import com.wingedtech.common.cache.guava.GuavaCacheManager;
import com.wingedtech.common.multitenancy.cache.MultiTenantGuavaCacheManager;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author zhangyp
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GuavaCacheAutoConfiguration.class, MultiTenantCacheAutoConfiguration.class})
@ActiveProfiles("disabled-multi-tenant-guava-cache")
public class GuavaCacheConfigurationTest {

    @Autowired(required = false)
    private GuavaCacheManager guavaCacheManager;

    @Autowired(required = false)
    private MultiTenantGuavaCacheManager multiTenantGuavaCacheManager;

    @Test
    public void testCacheManager(){
        assertThat(multiTenantGuavaCacheManager).isNull();
        assertThat(guavaCacheManager).isNotNull();
    }

    @Test
    public void testGuavaCacheNames() {
        assertThat(guavaCacheManager.getCacheNames()).containsExactlyInAnyOrder("cache1", "cache2", "cache3");
    }

}
