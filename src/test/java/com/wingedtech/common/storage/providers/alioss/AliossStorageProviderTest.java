package com.wingedtech.common.storage.providers.alioss;

import com.aliyun.oss.OSSClient;
import com.wingedtech.common.storage.ObjectStorageItem;
import com.wingedtech.common.storage.ObjectStorageItemAccessOptions;
import com.wingedtech.common.storage.ObjectStorageType;
import com.wingedtech.common.storage.StorageProviderTestBase;
import com.wingedtech.common.storage.providers.alioss.AliossStorageServiceConfiguration;
import com.wingedtech.common.storage.providers.alioss.AliossStorageServiceProperties;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AliossStorageServiceConfiguration.class, AliossStorageServiceProperties.class})
public class AliossStorageProviderTest extends StorageProviderTestBase {
    @Autowired
    AliossStorageServiceProperties aliossStorageServiceProperties;

    @Test
    public void testProperties() {
        assertThat(aliossStorageServiceProperties).isNotNull();
        assertThat(aliossStorageServiceProperties.getPublicResource()).isNotNull();
        assertThat(aliossStorageServiceProperties.getPrivateResource()).isNotNull();
    }

    @Test
    @Override
    public void testPutAndGet() throws IOException {
        super.testPutAndGet();
    }

    @Test
    public void testGetWithStyle() throws IOException {
        ObjectStorageItem objectStorageItem = new ObjectStorageItem(ObjectStorageType.PUBLIC_RESOURCE, TEST_IMAGE_PATH);
        final ObjectStorageItemAccessOptions options = new ObjectStorageItemAccessOptions();
        options.setImageStyle("unittest");
        System.out.println(provider.getObjectAccessUrl(objectStorageItem, options));

        objectStorageItem = new ObjectStorageItem(ObjectStorageType.PRIVATE_RESOURCE, TEST_IMAGE_PATH);
        System.out.println(provider.getObjectAccessUrl(objectStorageItem, options));
    }

    @Test
    public void testGetManyTimes() throws IOException {
        final OSSClient internalClient = ((AliossStorageProvider) provider).getInternalClient(ObjectStorageType.PUBLIC_RESOURCE);
        ObjectStorageItem objectStorageItem = new ObjectStorageItem(ObjectStorageType.PUBLIC_RESOURCE, TEST_OBJECT_PATH);
        final int testIterationCount = 6;
        for (int i = 0; i < testIterationCount; i++) {
            // 主动GC, 让之前的inputStream被GC时自动关闭
            System.gc();
            System.out.println("Test iteration: " + i);

            if (i % 10 == 0) {
                System.out.println("Test iteration: " + i);

            }
            InputStream inputStream = provider.getObject(objectStorageItem);
            assertThat(inputStream).isNotNull();
            // 注:ImageIO.read方法并不会主动关闭inputStream
            BufferedImage commodityImage = ImageIO.read(inputStream);
        }
        System.out.println("Test completed: " + testIterationCount);
    }

    @Test
    public void testSomethingNoSuchKey() {
        ObjectStorageItem objectStorageItem = ObjectStorageItem.get(ObjectStorageType.PRIVATE_RESOURCE, "examination-file/61a761f28d860507486b9e31/ecg-file-data.exa1");
        assertThat(provider.doesObjectExist(objectStorageItem)).isFalse();
    }

    @Test
    public void testAppend() throws IOException {
        super.testAppend();
    }


}
