package com.wingedtech.common.storage;

import com.wingedtech.common.lang.DateUtils;
import com.wingedtech.common.lang.TimeUtils;
import com.wingedtech.common.time.DateTimeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class StorageProviderTestBase {
    protected final static String TEST_IMAGE_PATH = "test.jpg";
    public final static String TEST_OBJECT_PATH = "1/test.txt";
    public final static String TEST_OBJECT_APPEND_PATH = "1/test_append.txt";
    @Autowired
    protected ObjectStorageProvider provider;

    public void testPutAndGet() throws IOException {
        String payload = Instant.now().toString();

        Instant beforeCreate = Instant.now();

        beforeCreate = Instant.ofEpochMilli(beforeCreate.toEpochMilli() / 1000 * 1000);

        ObjectStorageItem objectStorageItem = new ObjectStorageItem(ObjectStorageType.PUBLIC_RESOURCE, TEST_OBJECT_PATH);
        InputStream stream = StorageTestUtils.createTempFileStream(payload);
        assertThat(provider.pubObject(stream, objectStorageItem)).isEqualTo(TEST_OBJECT_PATH);

        System.out.println(provider.getObjectAccessUrl(objectStorageItem, new ObjectStorageItemAccessOptions()));

        InputStream inputStream = provider.getObject(objectStorageItem);
        assertThat(inputStream).isNotNull();
        assertThat(StorageTestUtils.readStreamContentOneLine(inputStream)).isEqualTo(payload);

        Instant lastModifiedDate = provider.getLastModifiedDate(objectStorageItem);
        System.out.println(lastModifiedDate);
        assertThat(lastModifiedDate).isAfterOrEqualTo(beforeCreate);
    }

    public void testAppend() throws IOException {

        ObjectStorageItem objectStorageItem = new ObjectStorageItem(ObjectStorageType.PUBLIC_RESOURCE, TEST_OBJECT_APPEND_PATH);
        objectStorageItem.setPath(TEST_OBJECT_APPEND_PATH);

        // ??????????????????????????????????????????
        if (provider.doesObjectExist(objectStorageItem)) {
            provider.delete(objectStorageItem);
        }

        StringBuilder payloadAppend = new StringBuilder();

        // ??????2????????????position?????????.?????????2??????????????????
        Long position = null;
        String payload = Instant.now().toString();
        payloadAppend.append(payload);
        provider.appendObject(objectStorageItem, payload.getBytes(StandardCharsets.UTF_8), position);

        for (int i = 0; i < 2; i++) {
            payload = Instant.now().toString();
            payloadAppend.append(payload);
            ObjectAppendResult objectAppendResult = provider.appendObject(objectStorageItem, payload.getBytes(StandardCharsets.UTF_8), position);
            position = objectAppendResult.getNextPosition();
        }

        // ????????????????????????
        InputStream inputStream = provider.getObject(objectStorageItem);
        assertThat(inputStream).isNotNull();
        assertThat(StorageTestUtils.readStreamContentOneLine(inputStream)).isEqualTo(payloadAppend.toString());
    }
}
