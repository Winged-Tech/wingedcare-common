package com.wingedtech.common.storage;

import com.wingedtech.common.autoconfigure.storage.ObjectStorageServiceConfiguration;
import com.wingedtech.common.storage.config.ObjectStorageResourceConfigProperties;
import com.wingedtech.common.storage.config.ObjectStorageResourceProperties;
import com.wingedtech.common.storage.config.UploadingPolicyKeys;
import com.wingedtech.common.storage.providers.alioss.AliossStorageServiceConfiguration;
import com.wingedtech.common.storage.rest.StorageResource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AliossStorageServiceConfiguration.class, ObjectStorageServiceConfiguration.class})
@Slf4j
public class ObjectDiskSpaceServiceTest {
    @Autowired
    ObjectStorageResourceConfigProperties configProperties;

    @Autowired
    ObjectStorageService service;

    StorageResource storageResource;

    @BeforeEach
    public void setup() {
        storageResource = new StorageResource(service);
    }

    @Test
    public void testResourceConfigProperties() {
        assertThat(configProperties).isNotNull();
        ObjectStorageResourceProperties defaultConfig = configProperties.getDefault();
        assertThat(defaultConfig).isNotNull();
        assertThat(defaultConfig.getAppendObjectId()).isFalse();
        assertThat(defaultConfig.getKeepOriginalFileName()).isFalse();

        ObjectStorageResourceProperties storeConfig = configProperties.getResourceConfig("store");
        assertThat(storeConfig).isNotNull();
        assertThat(storeConfig.getKeepOriginalFileName()).isFalse();
        assertThat(storeConfig.getAppendObjectId()).isTrue();
        assertThat(storeConfig.getPrefix()).isEqualTo("stores");
    }

    @Test
    public void testPutAndGet() throws IOException, URISyntaxException {
        String content = "ObjectStorageService_test";
        String name = "ObjectStorageService_test.txt";
        String objectId = "test-store";
        String resourceName = "store";

        InputStream file = StorageTestUtils.createTempFileStream(content);
        ObjectStorageItem item = ObjectStorageService.newStorageItemToPutWithObjectId(resourceName, objectId, ObjectStorageType.PUBLIC_RESOURCE, name);
        service.pubObject(file, item);

        assertThat(item.getStoragePath()).isNotBlank();
        System.out.println(String.format("File uploaded to %s", item.getStoragePath()));

        InputStream getFile = service.getObject(item);
        assertThat(StorageTestUtils.readStreamContentOneLine(getFile)).isEqualTo(content);

        String url = service.getObjectAccessUrl(item.getStoragePath());
        assertThat(url).isNotEmpty();
        System.out.println(url);

        url = service.getObjectAccessUrl(ObjectStorageService.newStorageItemToGet(resourceName, item.getStoragePath()));
        assertThat(url).isNotEmpty();
        System.out.println(url);

        ResponseEntity<String> responseEntity = storageResource.redirectToResource(item.getStoragePath(), resourceName, name, true);
        assertThat(responseEntity).isNotNull();
        System.out.println(responseEntity.getBody());

        responseEntity = storageResource.redirectToResource(item.getStoragePath(), ObjectStorageType.PRIVATE_RESOURCE, name, false, null);
        assertThat(responseEntity).isNotNull();
        System.out.println(responseEntity.getBody());
    }

    @Test
    public void testUnconfiguredResource() throws IOException {
        ObjectStorageItem item = ObjectStorageService.newStorageItemToPut("unconfigured", ObjectStorageType.PUBLIC_RESOURCE, "test");
        String content = "test";
        InputStream file = StorageTestUtils.createTempFileStream(content);
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> service.putObject(file, item));
    }

    @Test
    public void directUploadPolicy() {
        ObjectStorageItem item = new ObjectStorageItem();
        item.setResourceConfig("original-name");
        Map<String, String> policy = service.getDirectUploadingPolicy(item);
        log.info("Policy: {}", policy);
        assertThat(policy).containsKey(UploadingPolicyKeys.PATH);

        ObjectStorageItem item2 = new ObjectStorageItem();
        item2.setResourceConfig("store");
        item2.setObjectId("test-id");
        Map<String, String> policy2 = service.getDirectUploadingPolicy(item2);
        log.info("Policy: {}", policy2);
        assertThat(policy2).containsKey(UploadingPolicyKeys.PATH);
    }
    @Test
    public void testTemporaryAccessToken() {
        Map<String, String> token = service.getTemporaryAccessToken("original-name");
        log.info("sts-token: {}", token);
    }
}
