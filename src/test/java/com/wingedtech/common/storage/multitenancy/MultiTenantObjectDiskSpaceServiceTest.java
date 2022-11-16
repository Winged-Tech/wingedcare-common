package com.wingedtech.common.storage.multitenancy;

import com.wingedtech.common.autoconfigure.config.ConfigServiceConfiguration;
import com.wingedtech.common.autoconfigure.multitenancy.MultiTenancyConfiguration;
import com.wingedtech.common.autoconfigure.storage.ObjectStorageServiceConfiguration;
import com.wingedtech.common.multitenancy.util.TemporaryTenantContext;
import com.wingedtech.common.storage.*;
import com.wingedtech.common.storage.config.ObjectStorageResourceProperties;
import com.wingedtech.common.storage.multitenancy.MultiTenantObjectStorageItemPreprocessor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author taozhou
 * @date 2020/10/9
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ConfigServiceConfiguration.class, MultiTenancyConfiguration.class, ObjectStorageServiceConfiguration.class})
@ActiveProfiles("mt-storage-file")
@Slf4j
public class MultiTenantObjectDiskSpaceServiceTest {

    public static final String TENANT_1 = "tenant1";
    public static final String TENANT_2 = "tenant2";
    public static final String TEST_WITH_MT = "test-with-mt";
    public static final String TEST_WITHOUT_MT = "test-without-mt";
    @Autowired
    ObjectStorageService service;

    @Autowired
    ObjectStorageItemPreprocessor preprocessor;

    @Test
    public void testServiceConfig() {
        assertThat(service).isNotNull();
        assertThat(preprocessor).isNotNull().isOfAnyClassIn(MultiTenantObjectStorageItemPreprocessor.class);
    }

    @Test
    public void testMultiTenantPreprocessor() {
        ObjectStorageResourceProperties config = new ObjectStorageResourceProperties();
        config.setAppendObjectId(false);
        config.setKeepOriginalFileName(true);
        String prefix = "test";
        config.setPrefix(prefix);
        config.setResourceName("test");
        config.setMultiTenant(true);
        String separator = config.getPathSeparator();

        String testFilename = "test_image.png";
        ObjectStorageItem item = new ObjectStorageItem(ObjectStorageType.PUBLIC_RESOURCE, testFilename);
        item.setObjectId(UUID.randomUUID().toString());

        try (TemporaryTenantContext ignored = new TemporaryTenantContext(TENANT_1)) {
            item = preprocessor.preprocess(item, config);
        }

        assertThat(item.getPath()).isEqualTo(TENANT_1 + separator + prefix + separator + testFilename);

        ObjectStorageItem item2 = new ObjectStorageItem(ObjectStorageType.PUBLIC_RESOURCE, testFilename);
        item.setObjectId(UUID.randomUUID().toString());
        config.setMultiTenant(false);

        try (TemporaryTenantContext ignored = new TemporaryTenantContext(TENANT_1)) {
            item2 = preprocessor.preprocess(item2, config);
        }

        assertThat(item2.getPath()).isEqualTo(prefix + separator + testFilename);
    }

    public final static String TEST_OBJECT_PATH = "test.txt";

    @Test
    public void testPutAndGet() throws IOException {
        String payload = UUID.randomUUID().toString();

        ObjectStorageItem itemSavedInTenant1;

        try (TemporaryTenantContext ignored = new TemporaryTenantContext(TENANT_1)) {
            itemSavedInTenant1 = ObjectStorageItem.put(TEST_WITH_MT, TEST_OBJECT_PATH);
            InputStream stream = StorageTestUtils.createTempFileStream(payload);
            String objectPath = service.putObject(stream, itemSavedInTenant1);
            Assertions.assertThat(objectPath).startsWith(TENANT_1).contains(TEST_OBJECT_PATH);

            String objectAccessUrl = service.getObjectAccessUrl(itemSavedInTenant1, new ObjectStorageItemAccessOptions());
            System.out.println(objectAccessUrl);
            assertThat(objectAccessUrl).contains(TENANT_1);

            ObjectStorageItem itemToGet = ObjectStorageItem.get(TEST_WITH_MT, itemSavedInTenant1.getPath());
            InputStream inputStream = service.getObject(itemToGet);
            Assertions.assertThat(inputStream).isNotNull();
            Assertions.assertThat(StorageTestUtils.readStreamContentOneLine(inputStream)).isEqualTo(payload);

            ObjectStorageItem objectStorageItem2 = ObjectStorageItem.put(TEST_WITHOUT_MT, TEST_OBJECT_PATH);
            String objectPath2 = service.putObject(StorageTestUtils.createTempFileStream(payload), objectStorageItem2);
            Assertions.assertThat(objectPath2).doesNotStartWith(TENANT_1).contains(TEST_OBJECT_PATH);
            assertThat(service.getObjectAccessUrl(objectStorageItem2, new ObjectStorageItemAccessOptions())).doesNotContain(TENANT_1);

            ObjectStorageItem itemToGet2 = ObjectStorageItem.get(TEST_WITHOUT_MT, objectPath2);
            InputStream inputStream2 = service.getObject(itemToGet2);
            Assertions.assertThat(inputStream2).isNotNull();
            Assertions.assertThat(StorageTestUtils.readStreamContentOneLine(inputStream2)).isEqualTo(payload);
        }

        // 测试在tenant2环境下打开tenant1的文件
        try (TemporaryTenantContext ignored2 = new TemporaryTenantContext(TENANT_2)) {
            InputStream inputStream = service.getObject(itemSavedInTenant1);
            Assertions.assertThat(inputStream).isNotNull();
            Assertions.assertThat(StorageTestUtils.readStreamContentOneLine(inputStream)).isEqualTo(payload);
        }
    }
}
