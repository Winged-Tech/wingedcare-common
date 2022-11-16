package com.wingedtech.common.cache.config;

import com.google.common.cache.CacheBuilder;
import com.wingedtech.common.cache.guava.GuavaCache;
import com.wingedtech.common.constant.ConfigConstants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

import static com.wingedtech.common.migrations.Constants.CONFIG_ROOT;

/**
 * GuavaCache相关配置
 *
 * @author zhangyp
 */
@Data
public class GuavaCacheProperties {

    /**
     * cache名称
     */
    private String name;

    /**
     * 是否允许空Value(默认为true)
     */
    private Boolean allowNullValues;

    /**
     * 特殊配置
     */
    private String spec;

    /**
     * 默认允许空Value
     *
     * @return
     */
    public Boolean getAllowNullValues() {
        return null != allowNullValues ? allowNullValues : true;
    }

    public GuavaCache buildGuavaCache() {
        if (StringUtils.hasText(this.spec)) {
            return new GuavaCache(this.name, CacheBuilder.from(spec), getAllowNullValues());
        } else {
            return new GuavaCache(this.name, getAllowNullValues());
        }
    }
}
