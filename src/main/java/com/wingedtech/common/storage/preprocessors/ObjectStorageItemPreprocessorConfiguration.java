package com.wingedtech.common.storage.preprocessors;

import com.wingedtech.common.storage.ObjectStorageItemPreprocessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author taozhou
 * @date 2020/10/9
 */
@Configuration
public class ObjectStorageItemPreprocessorConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ObjectStorageItemPreprocessor preprocessor() {
        return new DefaultObjectStorageItemPreprocessor();
    }
}
