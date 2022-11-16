package com.wingedtech.common.storage.providers.alioss;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.internal.OSSUtils;
import com.aliyun.oss.model.*;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.common.collect.Maps;
import com.wingedtech.common.errors.BusinessException;
import com.wingedtech.common.storage.*;
import com.wingedtech.common.storage.config.ObjectStorageResourceProperties;
import com.wingedtech.common.storage.config.UploadingPolicyKeys;
import com.wingedtech.common.storage.errors.ObjectStorageException;
import com.wingedtech.common.storage.errors.ObjectStorageItemNotFoundException;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.time.Instant;
import java.util.*;

/**
 * StorageProvider using aliyun OSS service.
 *
 * @author taozhou
 */
public class AliossStorageProvider implements ObjectStorageProvider {
    private static final Logger logger = LoggerFactory.getLogger(AliossStorageProvider.class);
    public static final char URI_SEPERATOR = '/';

    private final AliossStorageServiceProperties properties;
    /**
     * 存储外部使用的OSSClient的map
     */
    private Map<ObjectStorageType, OSSClient> externalClients;
    /**
     * 存储内部使用的OSSClient的map
     */
    private Map<ObjectStorageType, OSSClient> internalClients;

    public AliossStorageProvider(AliossStorageServiceProperties properties) {
        this.properties = properties;
        externalClients = new HashMap<>();
        internalClients = new HashMap<>();
    }


    /**
     * 将一个对象存放到存储环境中，并且返回其对象相对路径
     *
     * @param stream
     * @param object 存储对象信息
     * @return
     */
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
        OSSClient client = getInternalClient(object.getType());
        AliossProperties properties = findAndCheckAliossProperties(object);
        client.putObject(properties.getBucketName(), path, stream);
        return path;
    }

    private AliossProperties findAndCheckAliossProperties(ObjectStorageItem object) {
        final ObjectStorageType objectStorageType = object.getType();
        return findAndCheckAliossPropertiesByType(objectStorageType);
    }

    private AliossProperties findAndCheckAliossPropertiesByType(ObjectStorageType objectStorageType) {
        final AliossProperties properties = this.properties.getProperties(objectStorageType);
        if (properties == null) {
            throw new IllegalStateException("Cannot find oss configuration for " + objectStorageType);
        }
        return properties;
    }

    /**
     * 从存储环境中获取一个对象的stream
     *
     * @param object 存储对象信息
     * @return
     */
    @Override
    public InputStream getObject(ObjectStorageItem object) {
        try {
            OSSClient client = getInternalClient(object.getType());
            AliossProperties properties = findAndCheckAliossProperties(object);
            OSSObject ossObject = client.getObject(properties.getBucketName(), object.getStoragePath());
            // 阿里云SDK文档要求:数据读取完成后，获取的流必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
            // https://help.aliyun.com/document_detail/84823.html
            // 因此, 使用org.apache.commons.io.input.AutoCloseInputStream包装一下, 即使调用方没有及时主动close, 当在这个stream对象被GC时或者读取完毕时也会自动关闭
//            return ossObject.getObjectContent();
            return new AutoCloseInputStream(ossObject.getObjectContent());
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

    /**
     * 获取网页直传policy
     *
     * @param object 存储对象信息
     * @return
     */
    @Override
    public Map<String, String> getDirectUploadingPolicy(ObjectStorageItem object, String prefix) {
        AliossProperties properties = findAndCheckAliossProperties(object);
        String accessId = properties.getAccessKeyId();
        String endpoint = properties.getEndPointExternalDomain();
        String bucket = properties.getBucketName();
        // host的格式为 bucketname.endpoint
        String host = "https://" + bucket + "." + endpoint;
        // 用户上传文件时指定的前缀。
        String dir = prefix;

        OSSClient client = getInternalClient(object.getType());
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            java.sql.Date expiration = new java.sql.Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, String> respMap = new LinkedHashMap<String, String>();
            respMap.put("accessid", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put(UploadingPolicyKeys.DIR, dir);
            respMap.put(UploadingPolicyKeys.HOST, host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            respMap.put("method", "POST");
            return respMap;
        } catch (Exception e) {
            logger.error("getDirectUploadingPolicy exception", e);
            return null;
        }
    }

    /**
     * 对指定的对象生成其访问url
     *
     * @param object 存储对象信息
     * @return
     */
    @Override
    public String getObjectAccessUrl(ObjectStorageItem object, ObjectStorageItemAccessOptions options) {
        if (object.getType() == null) {
            throw new IllegalArgumentException("The type of ObjectStorageItem cannot be null!");
        }
        AliossProperties properties = findAndCheckAliossProperties(object);
        if (object.getType() == ObjectStorageType.PUBLIC_RESOURCE) {
            if (options.isDownload()) {
                return getFInalGeneratePresignedUrl(object, options, properties);
            }
            return formatPublicResource(object, properties, options);
        } else {
            return getFInalGeneratePresignedUrl(object, options, properties);
        }
    }

    private String getFInalGeneratePresignedUrl(ObjectStorageItem object, ObjectStorageItemAccessOptions options, AliossProperties properties) {
        OSSClient client = getExternalClient(object.getType());
        Date date = Date.from(Instant.now().plusSeconds(properties.getSignedUrlExpirationSeconds()));

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(properties.getBucketName(), object.getStoragePath());
        request.setExpiration(date);

        ResponseHeaderOverrides headerOverrides = new ResponseHeaderOverrides();
        StringBuilder contentDisposition = new StringBuilder();
        if (options.isDownload()) {
            contentDisposition.append("attachment;");
        } else {
            contentDisposition.append("inline;");
        }
        if (StringUtils.isNotBlank(options.getFileName())) {
            contentDisposition.append(String.format("filename=%s", options.getFileName()));
        }
        headerOverrides.setContentDisposition(contentDisposition.toString());

        request.setResponseHeaders(headerOverrides);

        if (StringUtils.isNotBlank(options.getImageStyle())) {
            request.setProcess(getStylePresentation(options));
        }

        return client.generatePresignedUrl(request).toString();
    }

    public static String getStylePresentation(ObjectStorageItemAccessOptions options) {
        return "style/" + options.getImageStyle();
    }

    private String formatPublicResource(ObjectStorageItem object, AliossProperties properties, ObjectStorageItemAccessOptions options) {
        OSSClient client = getExternalClient(object.getType());
        URI finalEndpoint = OSSUtils.determineFinalEndpoint(client.getEndpoint(), properties.getBucketName(), client.getClientConfiguration());
        StringBuilder builder = new StringBuilder(finalEndpoint.toString());
        if (builder.charAt(builder.length() - 1) != URI_SEPERATOR) {
            builder.append(URI_SEPERATOR);
        }
        builder.append(object.getStoragePath());

        if (StringUtils.isNotBlank(options.getImageStyle())) {
            builder.append('?');
            builder.append("x-oss-process=").append(getStylePresentation(options));
        }

        return builder.toString();
    }

    OSSClient getExternalClient(ObjectStorageType type) {
        if (!externalClients.containsKey(type)) {
            externalClients.put(type, createExternalClient(type));
        }
        return externalClients.get(type);
    }

    private OSSClient createExternalClient(ObjectStorageType type) {
        AliossProperties properties = this.properties.getProperties(type);
        return new OSSClient(properties.getEndPointExternal(), properties.getAccessKeyId(), properties.getAccessKeySecret());
    }


    OSSClient getInternalClient(ObjectStorageType type) {
        if (!internalClients.containsKey(type)) {
            internalClients.put(type, createInternalClient(type));
        }
        return internalClients.get(type);
    }

    private OSSClient createInternalClient(ObjectStorageType type) {
        AliossProperties properties = this.properties.getProperties(type);
        ClientConfiguration config = new ClientConfiguration();
        // 设置maxConnections 5 可用于测试连接泄露问题
//        config.setMaxConnections(5);
        return new OSSClient(properties.getEndPointInternal(), new DefaultCredentialProvider(properties.getAccessKeyId(), properties.getAccessKeySecret()), config);
    }

    /**
     * 判断文件是否存在
     *
     * @param object
     * @return
     */
    @Override
    public Boolean doesObjectExist(ObjectStorageItem object) {
        AliossProperties properties = findAndCheckAliossProperties(object);
        OSSClient client = getInternalClient(object.getType());
        return client.doesObjectExist(properties.getBucketName(), object.getStoragePath());
    }

    @Override
    public void delete(ObjectStorageItem object) {
        AliossProperties properties = findAndCheckAliossProperties(object);
        OSSClient client = getInternalClient(object.getType());
        client.deleteObject(properties.getBucketName(), object.getPath());
    }

    @Override
    public Instant getLastModifiedDate(ObjectStorageItem object) {
        try {
            AliossProperties properties = findAndCheckAliossProperties(object);
            OSSClient client = getInternalClient(object.getType());
            SimplifiedObjectMeta meta = client.getSimplifiedObjectMeta(properties.getBucketName(), object.getStoragePath());
            if (meta != null) {
                return meta.getLastModified().toInstant();
            }
            return null;
        } catch (OSSException e) {
            throw translateOSSException(object, e);
        }
    }

    /**
     * 追加存储，返回追加结果
     *
     * @param object   存储对象信息
     * @param bytes    追加信息
     * @param position 追加位置
     * @return
     */
    @Override
    public ObjectAppendResult appendObject(ObjectStorageItem object, byte[] bytes, Long position) {

        if (bytes == null && bytes.length == 0) {
            throw new IllegalArgumentException("The input streams is null!");
        }

        if (!object.isStorageTypeSet()) {
            throw new IllegalArgumentException("The \"type\" field of the object is null or empty!");
        }
        String path = object.getStoragePath();
        if (StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("The \"path\" field of the object is null or empty!");
        }

        AliossProperties properties = findAndCheckAliossProperties(object);

        // 针对同一文件的追加存储增加同步锁
        synchronized (properties.getBucketName() + path) {
            // 若未指定追加位置则实时获取文件的追加位置
            try {
                position = getAppendPosition(object, position);
            } catch (NoSuchFieldException e) {
                logger.error("获取追加存储位置失败", e);
            } catch (IllegalAccessException e) {
                logger.error("获取追加存储位置失败", e);
            }
            if (position == null) {
                throw new BusinessException("获取追加存储位置失败");
            }

            // 通过AppendObjectRequest设置多个参数。
            AppendObjectRequest appendObjectRequest = new AppendObjectRequest(properties.getBucketName(), path, new ByteArrayInputStream(bytes));

            // 设置文件的追加位置
            appendObjectRequest.setPosition(position);

            // 追加写
            OSSClient client = getInternalClient(object.getType());
            AppendObjectResult appendObjectResult = client.appendObject(appendObjectRequest);

            return new ObjectAppendResult(path, appendObjectResult.getNextPosition());
        }
    }

    @Override
    public Map<String, String> getTemporaryAccessToken(ObjectStorageResourceProperties resourceConfig) {
        final ObjectStorageType storageType = resourceConfig.getStorageType();
        final AliossProperties aliossProperties = findAndCheckAliossPropertiesByType(storageType);
        Map<String, String> temporaryAccessToken = Maps.newHashMapWithExpectedSize(5);
        // 使用阿里云的STS能力, 获取临时的相关信息并以Map形式返回
        /**
         * 前端代码参考: (需要的参数)
         * const client = new OSS({
         *          // yourRegion填写Bucket所在地域。以华东1（杭州）为例，Region填写为oss-cn-hangzhou。
         *          region: 'yourRegion',
         *          // 从STS服务获取的临时访问密钥（AccessKey ID和AccessKey Secret）。
         *          accessKeyId: 'yourAccessKeyId',
         *          accessKeySecret: 'yourAccessKeySecret',
         *          // 从STS服务获取的安全令牌（SecurityToken）。
         *         stsToken: 'yourSecurityToken',
         *         // 填写Bucket名称，例如examplebucket。
         *         bucket: "examplebucket",
         *       });
         */
        String ossRegion = aliossProperties.getRegion();
        temporaryAccessToken.put("region", ossRegion);
        temporaryAccessToken.put("bucket", aliossProperties.getBucketName());
        AssumeRoleResponse ossToken = createOssToken(aliossProperties);
        if (Objects.isNull(ossToken)) {
            throw new BusinessException("获取 OSS 签名授权失败");
        }
        AssumeRoleResponse.Credentials credentials = ossToken.getCredentials();
        temporaryAccessToken.put("accessKeyId", credentials.getAccessKeyId());
        temporaryAccessToken.put("accessKeySecret", credentials.getAccessKeySecret());
        temporaryAccessToken.put("stsToken", credentials.getSecurityToken());
        return temporaryAccessToken;
    }

    /**
     * 生成STS临时凭据
     * @return
     */
    private AssumeRoleResponse createOssToken(AliossProperties aliossProperties) {
        String endpoint = aliossProperties.getEndPointSTS();
        String roleSessionName = "alice";
        String accessKeyId = aliossProperties.getAccessKeyId();
        String roleArn = aliossProperties.getRoleArn();
        String accessKeySecret = aliossProperties.getAccessKeySecret();
        try {
            DefaultProfile.addEndpoint("", "Sts", endpoint);
            IClientProfile profile = DefaultProfile.getProfile("", accessKeyId, accessKeySecret);
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setSysMethod(MethodType.POST);
            request.setSysProtocol(ProtocolType.HTTPS);
            request.setDurationSeconds(1800L);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            return client.getAcsResponse(request);
        } catch (ClientException e) {
            logger.error("创建阿里云 OSS 临时token异常", e);
        }
        return null;
    }

    /**
     * 获取文件追加位置
     *
     * @param object   存储对象信息
     * @param position 追加位置
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private Long getAppendPosition(ObjectStorageItem object, Long position) throws NoSuchFieldException, IllegalAccessException {

        // 不存在的文件，则无需拼接
        if (!doesObjectExist(object)) {
            return 0L;
        }

        // 有准确的追加位置则按追加位置进行
        if (position != null) {
            return position;
        }

        // 创建 headObject 请求
        AliossProperties properties = findAndCheckAliossProperties(object);
        HeadObjectRequest request = new HeadObjectRequest(properties.getBucketName(), object.getPath());

        OSSClient client = getInternalClient(object.getType());
        ObjectMetadata objectMetadata = client.headObject(request);

        // 通过反射获取
        Field metadataField = ObjectMetadata.class.getDeclaredField("metadata");
        metadataField.setAccessible(true);
        Map<String, Object> metadata = (Map<String, Object>) metadataField.get(objectMetadata);
        String positionStr = (String) metadata.get("x-oss-next-append-position");
        if (positionStr == null) {
            throw new BusinessException("获取追加存储位置失败");
        }
        return Long.parseLong(positionStr);
    }
}
