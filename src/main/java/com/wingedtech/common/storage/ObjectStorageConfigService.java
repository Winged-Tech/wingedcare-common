package com.wingedtech.common.storage;

import com.wingedtech.common.storage.config.ObjectStorageResourceConfigProperties;
import com.wingedtech.common.storage.config.ObjectStorageResourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

@Slf4j
public class ObjectStorageConfigService {
    protected final ObjectStorageResourceConfigProperties configProperties;
    private final ObjectStorageItemPreprocessor preprocessor;

    public ObjectStorageConfigService(ObjectStorageItemPreprocessor preprocessor, ObjectStorageResourceConfigProperties configProperties) {
        this.preprocessor = preprocessor;
        this.configProperties = configProperties;
    }

    public void preprocessItemType(@NotNull ObjectStorageItem object) {
        preprocessor.processItemStorageType(object, getResourceConfig(object));
    }

    /**
     * 对指定的ObjectStorageItem进行预处理，得到完整的对象信息，可使用putObjectWithoutPreprocess方法直接提交上传
     *
     * @param objectStorageItem
     * @return
     */
    public ObjectStorageItem preprocess(ObjectStorageItem objectStorageItem) {
        if (objectStorageItem == null) {
            throw new IllegalArgumentException("objectStorageItem is null");
        }
        if (StringUtils.isBlank(objectStorageItem.getResourceConfig())) {
            throw new IllegalArgumentException(("The resourceConfig property of the ObjectStorageItem is blank!"));
        }
        ObjectStorageResourceProperties config = getResourceConfig(objectStorageItem);
        return preprocessor.preprocess(objectStorageItem, config);
    }

    public ObjectStorageResourceProperties getResourceConfig(ObjectStorageItem objectStorageItem) {
        return getResourceConfig(objectStorageItem.getResourceConfig());
    }

    public ObjectStorageResourceProperties getResourceConfig(String resourceConfig) {
        ObjectStorageResourceProperties config = configProperties.getResourceConfig(resourceConfig);
        if (config == null) {
            throw new IllegalStateException(String.format("ObjectStorageResource config \"%s\" is not configured!", resourceConfig));
        }
        return config;
    }
}
