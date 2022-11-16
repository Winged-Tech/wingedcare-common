package com.wingedtech.common.autoconfigure.multitenancy.cache;


import com.wingedtech.common.autoconfigure.multitenancy.service.TenantInformationServiceConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnClass(CacheManager.class)
@EnableConfigurationProperties(CacheProperties.class)
@AutoConfigureAfter(TenantInformationServiceConfiguration.class)
@Import(MultiTenantCacheAutoConfiguration.CacheConfigurationImportSelector.class)
public class MultiTenantCacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CacheManagerCustomizers cacheManagerCustomizers(
            ObjectProvider<List<CacheManagerCustomizer<?>>> customizers) {
        return new CacheManagerCustomizers(customizers.getIfAvailable());
    }

    /**
     * {@link ImportSelector} to add {@link CacheType} configuration classes.
     */
    static class CacheConfigurationImportSelector implements ImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            CacheType[] types = CacheType.values();
            List<String> imports = new ArrayList<>();
            for (int i = 0; i < types.length; i++) {
                final String configurationClass = MultiTenantCacheConfigurations.getConfigurationClass(types[i]);
                if (configurationClass != null) {
                    imports.add(configurationClass);
                }
            }
            return imports.toArray(new String[0]);
        }

    }
}
