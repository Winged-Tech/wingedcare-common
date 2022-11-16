package com.wingedtech.common.multitenancy.service.information;

import com.wingedtech.common.multitenancy.domain.TenantInformation;

import java.util.List;

/**
 * 租户信息存储接口，用于实现支持多种不同方式存储和获取租户信息（例如，从profile配置、或者数据库、或者redis缓存）
 */
public interface TenantInformationStore {
    /**
     * 获取所有租户信息
     * @return
     */
    List<TenantInformation> getAll();
}
