package com.wingedtech.common.storage;

import com.wingedtech.common.storage.config.ObjectStorageResourceProperties;

/**
 * 对ObjectStorageItem进行一些使用前的预处理
 */
public interface ObjectStorageItemPreprocessor {
    ObjectStorageItem preprocess(ObjectStorageItem item, ObjectStorageResourceProperties resourceConfig);

    void processItemStorageType(ObjectStorageItem item, ObjectStorageResourceProperties resourceConfig);

    void processItemStoragePath(ObjectStorageItem item, ObjectStorageResourceProperties resourceConfig);
}
