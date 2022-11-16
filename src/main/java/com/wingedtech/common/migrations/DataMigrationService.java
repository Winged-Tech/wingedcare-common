package com.wingedtech.common.migrations;

import com.wingedtech.common.errors.BusinessException;
import com.wingedtech.common.multitenancy.Tenant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DataMigrationService {
    private final Map<String, DataProcessService> dataProcessServiceMap;

    public DataMigrationService(@NotNull List<DataProcessService> services) {
        this.dataProcessServiceMap = DataProcessorUtils.listToMap(services);
    }

    public void runMigration(String name) {
        log.info("请求执行migration: {}", name);
        getServiceByNameWithException(name).startProcessAsync(Tenant.getCurrentTenantIdOrMaster());
    }

    public DataProcessService getServiceByNameWithException(String name) {
        DataProcessService service = getServiceByName(name);
        if (service == null) {
            throw new BusinessException("找不到DataProcessService：" + name);
        }
        return service;
    }

    protected DataProcessService getServiceByName(String name) {
        return dataProcessServiceMap.get(name);
    }
}
