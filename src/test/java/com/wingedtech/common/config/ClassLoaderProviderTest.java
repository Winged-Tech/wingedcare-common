package com.wingedtech.common.config;

import com.wingedtech.common.autoconfigure.config.ClassLoaderConfigProviderConfiguration;
import com.wingedtech.common.autoconfigure.config.ConfigServiceConfiguration;
import com.wingedtech.common.autoconfigure.multitenancy.MultiTenancyConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MultiTenancyConfiguration.class, ConfigServiceConfiguration.class, ClassLoaderConfigProviderConfiguration.class})
@ActiveProfiles("config-resources")
public class ClassLoaderProviderTest {

    @Autowired
    ConfigService configService;

    @Test
    public void name() {
        PayServiceProperties payServiceProperties = configService.getTenantConfig("pay", PayServiceProperties.class);
        assertThat(payServiceProperties).isNotNull();
        assertThat(payServiceProperties.getChannels()).isNotEmpty();
        assertThat(payServiceProperties.getChannelProperties().containsKey(payServiceProperties.getChannels()[0]));
    }
}
