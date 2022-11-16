package com.wingedtech.common.storage.multitenancy;

import com.wingedtech.common.multitenancy.ConditionalOnMultiTenant;
import com.wingedtech.common.storage.ObjectStorageItemPreprocessor;
import com.wingedtech.common.storage.preprocessors.DefaultObjectStorageItemPreprocessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author taozhou
 * @date 2020/10/9
 */
@Configuration
@ConditionalOnMultiTenant
public class ObjectStorageServiceMultiTenantConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public ObjectStorageItemPreprocessor preprocessor() {
        return new MultiTenantObjectStorageItemPreprocessor(new DefaultObjectStorageItemPreprocessor());
    }
}
