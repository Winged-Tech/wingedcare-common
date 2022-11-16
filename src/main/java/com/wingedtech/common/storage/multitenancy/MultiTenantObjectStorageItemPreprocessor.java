package com.wingedtech.common.storage.multitenancy;

import com.wingedtech.common.multitenancy.Tenant;
import com.wingedtech.common.storage.ObjectStorageItem;
import com.wingedtech.common.storage.ObjectStorageItemPreprocessor;
import com.wingedtech.common.storage.config.ObjectStorageResourceProperties;

/**
 * @author taozhou
 * @date 2020/10/9
 */
public class MultiTenantObjectStorageItemPreprocessor implements ObjectStorageItemPreprocessor {

    public final ObjectStorageItemPreprocessor internalPreprocessor;

    public MultiTenantObjectStorageItemPreprocessor(ObjectStorageItemPreprocessor internalPreprocessor) {
        this.internalPreprocessor = internalPreprocessor;
    }

    @Override
    public ObjectStorageItem preprocess(ObjectStorageItem item, ObjectStorageResourceProperties resourceConfig) {
        processItemStorageType(item, resourceConfig);
        processItemStoragePath(item, resourceConfig);
        return item;
    }

    @Override
    public void processItemStorageType(ObjectStorageItem item, ObjectStorageResourceProperties resourceConfig) {
        internalPreprocessor.processItemStorageType(item, resourceConfig);
    }

    @Override
    public void processItemStoragePath(ObjectStorageItem item, ObjectStorageResourceProperties resourceConfig) {
        internalPreprocessor.processItemStoragePath(item, resourceConfig);

        if (resourceConfig.needsMultiTenant()) {
            String path = item.getPath();
            String pathSeparator = resourceConfig.getPathSeparator();
            // 尝试解析已存在的tenantId, 如果已存在有效的tenantId, 则不再重复添加tenantId
            String possibleTenantIdInPath = parseTenantIdFromPath(path, pathSeparator);
            if (possibleTenantIdInPath == null || !Tenant.isValidTenantId(possibleTenantIdInPath)) {
                String tenantId = Tenant.getCurrentTenantIdOrMaster();
                if (path.startsWith(tenantId) && path.startsWith(pathSeparator, tenantId.length())) {
                    return;
                }
                item.setPath(tenantId + pathSeparator + path);
            }
        }
    }

    public static String parseTenantIdFromPath(String path, String separator) {
        int separatorLength = separator.length();
        int startIndex = 0;
        // 寻找分隔符时防止path以separator开始
        if (path.startsWith(separator)) {
            startIndex = separatorLength;
        }
        int indexOfFirstSeparator = path.indexOf(separator, startIndex);
        if (indexOfFirstSeparator != -1) {
            return path.substring(startIndex, indexOfFirstSeparator);
        }
        return null;
    }
}
