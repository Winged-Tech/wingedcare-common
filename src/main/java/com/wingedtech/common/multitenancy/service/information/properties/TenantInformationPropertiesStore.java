package com.wingedtech.common.multitenancy.service.information.properties;

import com.wingedtech.common.multitenancy.domain.TenantInformation;
import com.wingedtech.common.multitenancy.service.information.TenantInformationStore;

import java.util.List;

/**
 * 从properties配置中读取租户信息的driver
 */
public class TenantInformationPropertiesStore implements TenantInformationStore {

    private final TenantInformationProperties tenantInformationProperties;

    public TenantInformationPropertiesStore(TenantInformationProperties tenantInformationProperties) {
        this.tenantInformationProperties = tenantInformationProperties;
    }

    @Override
    public List<TenantInformation> getAll() {
        return tenantInformationProperties.getTenants();
    }
}
