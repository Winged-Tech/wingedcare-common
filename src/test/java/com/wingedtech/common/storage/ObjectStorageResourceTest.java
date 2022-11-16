package com.wingedtech.common.storage;

import com.wingedtech.common.autoconfigure.storage.ObjectStorageServiceConfiguration;
import com.wingedtech.common.storage.config.ObjectStorageResourceConfigProperties;
import com.wingedtech.common.storage.providers.file.FileSystemStorageConfiguration;
import com.wingedtech.common.storage.rest.StorageResource;
import com.wingedtech.common.storage.store.ObjectStorageItemStoreConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URISyntaxException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ObjectStorageItemStoreConfiguration.class, FileSystemStorageConfiguration.class, ObjectStorageServiceConfiguration.class})
@ActiveProfiles("storage-file")
@Slf4j
public class ObjectStorageResourceTest extends StorageProviderTestBase {
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
    public void testRedirectToDownload() throws IOException, URISyntaxException {
        String payload = "test";
        String filename = "download.txt";

        final ObjectStorageType type = ObjectStorageType.PRIVATE_RESOURCE;
        ObjectStorageItem objectStorageItem = ObjectStorageItem.put("test", filename);
        String path = service.putObjectWithoutPreprocess(StorageTestUtils.createTempFileStream(payload), objectStorageItem);

        final ResponseEntity<String> response = storageResource.redirectToDownloadApi(path, type, filename, true);
        log.info(response.getBody());
    }
}
