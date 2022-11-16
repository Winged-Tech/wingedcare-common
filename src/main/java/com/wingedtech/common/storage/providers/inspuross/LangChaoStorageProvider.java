package com.wingedtech.common.storage.providers.inspuross;

import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.common.auth.HmacSHA1Signature;
import com.inspurcloud.oss.client.OSSClient;
import com.inspurcloud.oss.client.impl.OSSClientImpl;
import com.inspurcloud.oss.exception.OSSException;
import com.inspurcloud.oss.model.GeneratePresignedUrlRequest;
import com.inspurcloud.oss.model.ObjectMetadata;
import com.inspurcloud.oss.model.ResponseHeaderOverrides;
import com.inspurcloud.oss.model.entity.OSSObject;
import com.wingedtech.common.storage.*;
import com.wingedtech.common.storage.config.ObjectStorageResourceProperties;
import com.wingedtech.common.storage.config.UploadingPolicyKeys;
import com.wingedtech.common.storage.errors.ObjectStorageException;
import com.wingedtech.common.storage.errors.ObjectStorageItemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

/**
 * @author ssy
 * @date 2020/11/25 15:44
 */
@Slf4j
public class LangChaoStorageProvider implements ObjectStorageProvider {
    private static final Logger logger = LoggerFactory.getLogger(LangChaoStorageProvider.class);
    public static final char URI_SEPERATOR = '/';

    private final LangChaoStorageServiceProperties properties;
    /**
     * 存储外部使用的OSSClient的map
     */
    private Map<ObjectStorageType, OSSClient> externalClients;
    /**
     * 存储内部使用的OSSClient的map
     */
    private Map<ObjectStorageType, OSSClient> internalClients;

    public LangChaoStorageProvider(LangChaoStorageServiceProperties properties) {
        this.properties = properties;
        externalClients = new HashMap<>();
        internalClients = new HashMap<>();
    }

    @Override
    public String pubObject(InputStream stream, ObjectStorageItem object) {
        if (stream == null) {
            throw new IllegalArgumentException("The input streams is null!");
        }
        if (!object.isStorageTypeSet()) {
            throw new IllegalArgumentException("The \"type\" field of the object is null or empty!");
        }
        String path = object.getStoragePath();
        if (StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("The \"path\" field of the object is null or empty!");
        }
        //获取OSSClient实例
        OSSClient ossClient = getInternalClient(object.getType());
        LangChaoProperties properties = findAndCheckOssProperties(object);

        //流上传
        ossClient.putObject(properties.getBucketName(), path, stream);
        return path;
    }

    private LangChaoProperties findAndCheckOssProperties(ObjectStorageItem object) {
        final LangChaoProperties properties = this.properties.getProperties(object.getType());
        if (properties == null) {
            throw new IllegalStateException("Cannot find oss configuration for " + object.getType());
        }
        return properties;
    }

    private OSSClient getInternalClient(ObjectStorageType type) {
        if (!internalClients.containsKey(type)) {
            internalClients.put(type, createOssClient(type));
        }
        return internalClients.get(type);
    }

    private OSSClient createOssClient(ObjectStorageType type) {
        LangChaoProperties properties = this.properties.getProperties(type);
        return new OSSClientImpl(properties.getEndPointInternal(), properties.getAccessKeyId(), properties.getAccessKeySecret());
    }

    @Override
    public InputStream getObject(ObjectStorageItem object) {
        try {
            OSSClient client = getInternalClient(object.getType());
            LangChaoProperties properties = findAndCheckOssProperties(object);
            OSSObject ossObject = client.getObject(properties.getBucketName(), object.getStoragePath());
            return ossObject.getObjectContent();
        } catch (OSSException e) {
            throw translateOSSException(object, e);
        }
    }

    public ObjectStorageException translateOSSException(ObjectStorageItem object, OSSException e) {
        if (StringUtils.equals(e.getErrorCode(), OSSErrorCode.NO_SUCH_BUCKET)
            || StringUtils.equals(e.getErrorCode(), OSSErrorCode.NO_SUCH_KEY)) {
            return new ObjectStorageItemNotFoundException(e, object);
        } else {
            return new ObjectStorageException(e, object);
        }
    }

    @Override
    public Map<String, String> getDirectUploadingPolicy(ObjectStorageItem object, String prefix) {
        LangChaoProperties properties = findAndCheckOssProperties(object);

        Long expires = Instant.now().getEpochSecond() + properties.getSignedUrlExpirationSeconds();

        String resourcePath = "/" + properties.getBucketName() + "/" + object.getPath();

        String resourcePathForSignature = resourcePath;

        String host = "https://" + properties.getBucketName() + "." + properties.getEndPointInternalDomain() + "/" + object.getPath();

        resourcePath = EncodeUtils.urlEncode(resourcePath, true);

        /* 浪潮SDK内的签名样例 (Date模式签名, 不是expires模式):
        PUT

        application/octet-stream
        Thu, 07 Jan 2021 08:02:50 GMT
        /gtsms/1/test.txt
         */

        // AWS signing:
        // https://docs.aws.amazon.com/AmazonS3/latest/dev/RESTAuthentication.html#ConstructingTheAuthenticationHeader
        // https://docs.aws.amazon.com/AmazonS3/latest/API/sigv4-query-string-auth.html

        String data =
            "PUT\n"
                + "\n"
                + "application/octet-stream\n"
                + expires + "\n"
                // CanonicalizedOSSHeaders(headers不带\n)
                // CanonicalizedResource
                + resourcePathForSignature;

        logger.debug("Signature string: \n{}", data);

        String signatureStr = new HmacSHA1Signature().computeSignature(properties.getAccessKeySecret(),
            data);
        logger.debug("Signature is: {}", signatureStr);
        try {
            // 文档https://console1.cloud.inspur.com/document/oss/5-API/5.3-access-control/03-add-signature-in-url.html
            // 文档里的参数为OSSAccessKeyId, 但一直调不通,将OSSAccessKeyId换成AWSAccessKeyId以后上传成功了
            // 根据SDK内generatePresignedUrl的代码, 使用URLEncoder.encode(str, "GBK")方法进行encode
            host = host + "?AccessKeyId=" + properties.getAccessKeyId() + "&Expires=" + expires + "&Signature=" + URLEncoder.encode(signatureStr, "GBK");
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException", e);
            throw new ObjectStorageException("UnsupportedEncoding GBK", object);
        }
        Map<String, String> respMap = new LinkedHashMap<String, String>();
        respMap.put(UploadingPolicyKeys.HOST, host);
        respMap.put("method", "PUT");
        return respMap;
    }

    @Override
    public String getObjectAccessUrl(ObjectStorageItem object, ObjectStorageItemAccessOptions options) {
        if (object.getType() == null) {
            throw new IllegalArgumentException("The type of ObjectStorageItem cannot be null!");
        }
        LangChaoProperties properties = findAndCheckOssProperties(object);
        if (object.getType() == ObjectStorageType.PUBLIC_RESOURCE) {
            if (options.isDownload()) {
                return getFinalGenerateSignedUrl(object, options, properties);
            }
            return formatPublicResource(object, properties, options);
        }
        return getFinalGenerateSignedUrl(object, options, properties);
    }

    private String getFinalGenerateSignedUrl(ObjectStorageItem object, ObjectStorageItemAccessOptions options, LangChaoProperties properties) {
        OSSClient client = getExternalClient(object.getType());
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(properties.getBucketName(), object.getStoragePath(), Instant.now().getEpochSecond() + properties.getSignedUrlExpirationSeconds());
        if (options.isDownload()) {
            ResponseHeaderOverrides responseHeaders = generatePresignedUrlRequest.getResponseHeaders();
            StringBuilder builder = new StringBuilder();
            builder.append("attachment");
            if (StringUtils.isNotBlank(options.getFileName())) {
                builder.append(String.format(";filename=%s", options.getFileName()));
            }
            responseHeaders.setContentDisposition(builder.toString());
        }
        return client.generatePresignedUrl(generatePresignedUrlRequest);
    }

    private String formatPublicResource(ObjectStorageItem object, LangChaoProperties properties, ObjectStorageItemAccessOptions options) {
        OSSClient client = getExternalClient(object.getType());
        return client.getUrl(properties.getBucketName(), object.getPath()).toString();
    }

    private OSSClient getExternalClient(ObjectStorageType type) {
        if (!externalClients.containsKey(type)) {
            externalClients.put(type, createOssClient(type));
        }
        return externalClients.get(type);
    }

    @Override
    public Boolean doesObjectExist(ObjectStorageItem object) {
        LangChaoProperties properties = findAndCheckOssProperties(object);
        OSSClient ossClient = createOssClient(object.getType());
        return ossClient.doesObjectExist(properties.getBucketName(), object.getPath());
    }

    @Override
    public void delete(ObjectStorageItem object) {
        LangChaoProperties properties = findAndCheckOssProperties(object);
        OSSClient client = createOssClient(object.getType());
        client.deleteObject(properties.getBucketName(), object.getPath());
    }

    @Override
    public Instant getLastModifiedDate(ObjectStorageItem object) {
        try {
            LangChaoProperties properties = findAndCheckOssProperties(object);
            OSSClient client = createOssClient(object.getType());
            ObjectMetadata objectMetadata = client.getObjectMetadata(properties.getBucketName(), object.getStoragePath());
            if (objectMetadata != null) {
                return objectMetadata.getLastModified().toInstant();
            }
            return null;
        } catch (OSSException e) {
            throw translateOSSException(object, e);
        }
    }

    /**
     * 追加存储，并且返回其对象相对路径
     *
     * @param object   存储对象信息
     * @param bytes    追加信息
     * @param position 追加位置
     * @return
     */
    @Override
    public ObjectAppendResult appendObject(ObjectStorageItem object, byte[] bytes, Long position) {
        // TODO 待实现
        log.error("Unimplemented operation - appendObject");
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> getTemporaryAccessToken(ObjectStorageResourceProperties resourceConfig) {
        // TODO 待实现
        log.error("Unimplemented operation - getTemporaryAccessToken");
        throw new UnsupportedOperationException();
    }
}
