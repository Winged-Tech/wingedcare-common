package com.wingedtech.common.multitenancy.information;

import com.google.common.collect.Lists;
import com.wingedtech.common.autoconfigure.config.ConfigServiceConfiguration;
import com.wingedtech.common.autoconfigure.multitenancy.MultiTenancyConfiguration;
import com.wingedtech.common.multitenancy.Constants;
import com.wingedtech.common.multitenancy.Tenant;
import com.wingedtech.common.multitenancy.TenantInformationService;
import com.wingedtech.common.multitenancy.config.TenantGenericAppearanceConfig;
import com.wingedtech.common.multitenancy.domain.TenantInformation;
import com.wingedtech.common.multitenancy.domain.TenantPropertyKeys;
import com.wingedtech.common.multitenancy.service.information.properties.TenantInformationPropertiesStore;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ConfigServiceConfiguration.class, MultiTenancyConfiguration.class})
@ActiveProfiles("mt-properties")
public class TenantInformationPropertiesDriverTest {

    public static final String TENANT_1 = "tenant1";
    public static final String TENANT_2 = "tenant2";

    @Autowired
    TenantInformationPropertiesStore tenantInformationPropertiesDriver;

    @Autowired
    TenantInformationService tenantInformationService;

    @Test
    public void testDriver() {
        assertThat(tenantInformationPropertiesDriver).isNotNull();
        final List<TenantInformation> tenants = tenantInformationPropertiesDriver.getAll();
        assertThat(tenants).isNotEmpty();
    }

    @Test
    public void testService() {
        assertThat(tenantInformationService).isNotNull();
        assertThat(tenantInformationService.hasTenant(Constants.MASTER_TENANT)).isTrue();

        final String tenant1Id = TENANT_1;
        assertThat(tenantInformationService.hasTenant(tenant1Id)).isTrue();
        final TenantInformation tenant1 = tenantInformationService.getTenant(tenant1Id);
        assertThat(tenant1).isNotNull();
        assertThat(tenant1.getProperty(TenantPropertyKeys.MONGO_URI)).isNotBlank();
        assertThat(tenantInformationService.getTenantBySubDomain("dev-" + tenant1Id)).isNotNull();

        // 模拟当前租户信息
        Tenant.setCurrentTenantId(tenant1Id);
        assertThat(Tenant.getCurrentTenant()).isNotNull();
    }

    @Test
    public void testForeach() {
        assertThat(tenantInformationService).isNotNull();
        List<String> tenantIds = Lists.newArrayList();
        tenantInformationService.forEachTenant(tenantId -> {
            tenantIds.add(Tenant.getCurrentTenantId());
        });
        assertThat(tenantIds).containsExactlyInAnyOrder(Constants.MASTER_TENANT, TENANT_1, TENANT_2);
    }
}
