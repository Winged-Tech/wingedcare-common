package com.wingedtech.common.multitenancy.information;

import com.wingedtech.common.autoconfigure.config.ConfigServiceConfiguration;
import com.wingedtech.common.autoconfigure.multitenancy.MultiTenancyConfiguration;
import com.wingedtech.common.multitenancy.Tenant;
import com.wingedtech.common.multitenancy.TenantInformationService;
import com.wingedtech.common.multitenancy.config.TenantGenericAppearanceConfig;
import com.wingedtech.common.multitenancy.service.information.properties.TenantInformationPropertiesStore;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author taozhou
 * @date 2021/2/19
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ConfigServiceConfiguration.class, MultiTenancyConfiguration.class})
@ActiveProfiles("mt-properties")
public class TenantConfigTest {

    public static final String TENANT_1 = "tenant1";
    public static final String TENANT_2 = "tenant2";

    @Autowired
    TenantInformationService tenantInformationService;

    @Test
    public void testGenericAppearanceConfig() {
        Tenant.setCurrentTenantId(TENANT_1);
        final TenantGenericAppearanceConfig config1 = tenantInformationService.getTenantGenericAppearanceConfig();
        assertThat(config1).isNotNull();
        assertThat(config1.getLogoUrl()).isEqualTo("http://logos/tenant1");

        final String tenant2Id = TENANT_2;
        Tenant.setCurrentTenantId(tenant2Id);
        final TenantGenericAppearanceConfig config2 = tenantInformationService.getTenantGenericAppearanceConfig();
        assertThat(config2).isNotNull();
        assertThat(config2.getLogoUrl()).withFailMessage("租户<%s>在没有配置的情况下应该读取到default配置", tenant2Id).isEqualTo("http://logos/default");
    }
}
