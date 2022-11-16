package com.wingedtech.common.storage;

import com.wingedtech.common.storage.config.ObjectStorageResourceProperties;
import com.wingedtech.common.storage.preprocessors.DefaultObjectStorageItemPreprocessor;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class ObjectStorageItemPreprocessorTest {

    @Test
    public void testPreprocessor() {
        ObjectStorageResourceProperties config = new ObjectStorageResourceProperties();
        config.setAppendObjectId(true);
        config.setKeepOriginalFileName(false);
        config.setPrefix("test");
        config.setResourceName("test");

        ObjectStorageItem item = new ObjectStorageItem(ObjectStorageType.PUBLIC_RESOURCE, "test_image.png");
        item.setObjectId(UUID.randomUUID().toString());

        ObjectStorageItemPreprocessor preprocessor = new DefaultObjectStorageItemPreprocessor();
        item = preprocessor.preprocess(item, config);

        System.out.println(item.getStoragePath());
    }
}
