package com.wingedtech.common.storage;

import com.wingedtech.common.storage.providers.file.FileSystemStorageConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FileSystemStorageConfiguration.class})
@ActiveProfiles("storage-file")
@Slf4j
public class FileStorageProviderTest extends StorageProviderTestBase {

    @Test
    @Override
    public void testPutAndGet() throws IOException {
        super.testPutAndGet();
    }

    @Test
    public void directUploadPolicy() {
        final Map<String, String> directUploadingPolicy = provider.getDirectUploadingPolicy(ObjectStorageItem.get("test", TEST_OBJECT_PATH), "test");
        log.info("Policy: {}", directUploadingPolicy);
    }

    @Test
    public void testAppend() throws IOException {
        super.testAppend();
    }
}
