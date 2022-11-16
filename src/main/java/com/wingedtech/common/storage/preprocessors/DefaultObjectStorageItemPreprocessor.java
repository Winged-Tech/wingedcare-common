package com.wingedtech.common.storage.preprocessors;

import com.wingedtech.common.storage.ObjectStorageItem;
import com.wingedtech.common.storage.ObjectStorageItemPreprocessor;
import com.wingedtech.common.storage.config.ObjectStorageResourceProperties;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.UUID;

/**
 * @author taozhou
 * @date 2020/10/9
 */
public class DefaultObjectStorageItemPreprocessor implements ObjectStorageItemPreprocessor {
    /**
     * 对指定的ObjectStorageItem进行预处理。
     * @param item
     * @param resourceConfig 针对该item所使用的资源配置
     * @return
     */
    @Override
    public ObjectStorageItem preprocess(ObjectStorageItem item, ObjectStorageResourceProperties resourceConfig) {
        processItemStorageType(item, resourceConfig);
        processItemStoragePath(item, resourceConfig);
        return item;
    }

    /**
     * 对指定的ObjectStorageItem进行其存储类型处理。
     * @param item
     * @param resourceConfig
     */
    @Override
    public void processItemStorageType(ObjectStorageItem item, ObjectStorageResourceProperties resourceConfig) {
        if (item.isStorageTypeSet()) {
            return;
        }
        item.setType(resourceConfig.getStorageType());
    }

    /**
     * 对指定的ObjectStorageItem进行其存储路径处理，如果该item已有storage path，则不做人户处理。
     * @param item
     */
    @Override
    public void processItemStoragePath(ObjectStorageItem item, ObjectStorageResourceProperties resourceConfig) {
        if (item.isPathSet()) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(resourceConfig.getPrefix())) {
            builder.append(resourceConfig.getPrefix()).append(resourceConfig.getPathSeparator());
        }
        if (resourceConfig.getAppendObjectId()) {
            builder.append(item.getObjectId()).append(resourceConfig.getPathSeparator());
        }
        if (resourceConfig.getKeepOriginalFileName()) {
            if (resourceConfig.getAppendTimestampInFileName()) {
                final String extension = FilenameUtils.getExtension(item.getName());
                final String baseName = FilenameUtils.getBaseName(item.getName());
                builder.append(baseName).append(Instant.now().getNano()).append(".").append(extension);
            } else {
                builder.append(item.getName());
            }
        }
        else {
            builder.append(UUID.randomUUID().toString());
            String extension = FilenameUtils.getExtension(item.getName());
            if (StringUtils.isNotBlank(extension)) {
                builder.append(FilenameUtils.EXTENSION_SEPARATOR).append(extension);
            }
        }
        item.setPath(builder.toString());
    }
}
