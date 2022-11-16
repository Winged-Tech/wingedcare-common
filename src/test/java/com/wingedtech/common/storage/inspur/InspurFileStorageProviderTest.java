package com.wingedtech.common.storage.inspur;

import com.inspurcloud.oss.model.Credentials;
import com.inspurcloud.oss.model.HttpMethod;
import com.inspurcloud.oss.model.OSSRequest;
import com.inspurcloud.oss.model.SingleOkHttpEnum;
import com.inspurcloud.oss.parser.impl.ErrorParser;
import com.wingedtech.common.autoconfigure.storage.ObjectStorageServiceConfiguration;
import com.wingedtech.common.storage.*;
import com.wingedtech.common.storage.providers.inspuross.LangChaoStorageServiceConfiguration;
import com.wingedtech.common.storage.providers.inspuross.LangChaoStorageServiceProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Map;

import static com.inspurcloud.oss.client.impl.OSSOkHttpClient.buildUrlWithParamter;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LangChaoStorageServiceConfiguration.class, LangChaoStorageServiceProperties.class, ObjectStorageServiceConfiguration.class})
@ActiveProfiles("storage-file-inspur")
@Slf4j
public class InspurFileStorageProviderTest extends StorageProviderTestBase {

    @Autowired
    LangChaoStorageServiceProperties langChaoStorageServiceProperties;

    @Autowired
    ObjectStorageService objectStorageService;

    private static OkHttpClient okHttpClient;

    private static ErrorParser errorParser;

    @BeforeAll
    public static void prepare() {
        okHttpClient = SingleOkHttpEnum.OKHTTP.getOkHttpClient();
        errorParser = new ErrorParser();
    }

    @Test
    @Override
    public void testPutAndGet() throws IOException {
        super.testPutAndGet();
    }

    @Test
    public void directUploadPolicy() throws IOException {
        ObjectStorageItem test = ObjectStorageService.newStorageItemToGet("test", "1.txt");
        final Map<String, String> directUploadingPolicy = objectStorageService.getDirectUploadingPolicy(test);
        log.info("Policy: {}", directUploadingPolicy);

        String payload = Instant.now().toString();
        log.info("Payload is: {}", payload);
        InputStream stream = StorageTestUtils.createTempFileStream(payload);

        Request request;
        OSSRequest ossRequest = buildOssRequest(directUploadingPolicy.get("path"));
        ossRequest.setUrl(directUploadingPolicy.get("host"));
        ossRequest.getHeaders().put("Content-Type", "application/octet-stream");

        request = (new Request.Builder()).put(InspurFileStorageTestUtils.createRequestBodyFromInputStream(stream)).url(buildUrlWithParamter(ossRequest)).build();

        Response response = null;

        try {
            Request request1 = ModifiedInspurSignUtils.addCommonHeader(ossRequest, request, null, directUploadingPolicy.get("x-amz-date"));
            log.info("final request: {} {}", request1, request1.headers());
            response = okHttpClient.newCall(request1).execute();

            if (!response.isSuccessful()) {
                throw errorParser.parseError(response);
            }

            InputStream inputStream = provider.getObject(test);
            assertThat(inputStream).isNotNull();
            String actual = StorageTestUtils.readStreamContentOneLine(inputStream);
            log.info("Downloaded file content is:{}", actual);
            assertThat(actual).isEqualTo(payload);
        } catch (IOException var5) {
            log.error("Exception", var5);
        }
    }

    private OSSRequest buildOssRequest(String key) {
        return new OSSRequest(langChaoStorageServiceProperties.getPrivateResource().getBucketName(), key, new Credentials(langChaoStorageServiceProperties.getPrivateResource().getAccessKeyId(), langChaoStorageServiceProperties.getPrivateResource().getAccessKeySecret()), HttpMethod.PUT, langChaoStorageServiceProperties.getPrivateResource().getEndPointInternal());
    }

    @Test
    public void testProperties() {
        assertThat(langChaoStorageServiceProperties).isNotNull();
        assertThat(langChaoStorageServiceProperties.getPublicResource()).isNotNull();
        assertThat(langChaoStorageServiceProperties.getPrivateResource()).isNotNull();
    }

    @Test
    public void testGetWithStyle() throws IOException {
        ObjectStorageItem objectStorageItem = new ObjectStorageItem(ObjectStorageType.PUBLIC_RESOURCE, TEST_IMAGE_PATH);
        final ObjectStorageItemAccessOptions options = new ObjectStorageItemAccessOptions();
        options.setImageStyle("unittest");
        options.setDownload(true);
        System.out.println(provider.getObjectAccessUrl(objectStorageItem, options));

        objectStorageItem = new ObjectStorageItem(ObjectStorageType.PRIVATE_RESOURCE, TEST_IMAGE_PATH);
        System.out.println(provider.getObjectAccessUrl(objectStorageItem, options));
    }
}
