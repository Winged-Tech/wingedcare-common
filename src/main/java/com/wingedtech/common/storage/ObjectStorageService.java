package com.wingedtech.common.storage;

import com.google.common.base.Preconditions;
import com.wingedtech.common.storage.config.ObjectStorageResourceConfigProperties;
import com.wingedtech.common.storage.config.ObjectStorageResourceProperties;
import com.wingedtech.common.storage.config.UploadingPolicyKeys;
import com.wingedtech.common.storage.errors.ObjectStorageItemNotFoundException;
import com.wingedtech.common.storage.store.ObjectStorageItemStore;
import com.wingedtech.common.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 提供文件对象存储服务的全局service
 */
@Slf4j
public class ObjectStorageService {

    private final ObjectStorageProvider provider;
    private final ObjectStorageItemStore store;
    private final ObjectStorageConfigService objectStorageConfigService;

    public ObjectStorageService(ObjectStorageProvider provider, ObjectStorageResourceConfigProperties configProperties, ObjectStorageItemStore store, ObjectStorageConfigService objectStorageConfigService) {
        this.provider = provider;
        this.store = store;
        this.objectStorageConfigService = objectStorageConfigService;
    }

    /**
     * 创建一个ObjectStorageItem用于存储，不带objectId
     *
     * @param resourceName
     * @param type
     * @param name
     * @return
     */
    public static ObjectStorageItem newStorageItemToPut(String resourceName, ObjectStorageType type, String name) {
        return new ObjectStorageItem(resourceName, type, name);
    }

    /**
     * 创建一个ObjectStorageItem用于存储，不带objectId
     *
     * @param resourceName
     * @param name
     * @return
     */
    public static ObjectStorageItem newStorageItemToPut(String resourceName, String name) {
        return new ObjectStorageItem(resourceName, null, name);
    }

    /**
     * 创建一个ObjectStorageItem用于存储，并且带有objectId
     *
     * @param resourceName
     * @param objectId
     * @param type
     * @param name
     * @return
     */
    public static ObjectStorageItem newStorageItemToPutWithObjectId(String resourceName, String objectId, ObjectStorageType type, String name) {
        return new ObjectStorageItem(resourceName, objectId, type, name);
    }

    /**
     * 创建一个ObjectStorageItem用于存储，并且带有objectId
     *
     * @param resourceName
     * @param objectId
     * @param name
     * @return
     */
    public static ObjectStorageItem newStorageItemToPutWithObjectId(String resourceName, String objectId, String name) {
        return new ObjectStorageItem(resourceName, objectId, null, name);
    }

    /**
     * 创建一个ObjectStorageItem用于获取或者签名
     *
     * @param type
     * @param path
     * @return
     */
    public static ObjectStorageItem newStorageItemToGet(ObjectStorageType type, String path) {
        return new ObjectStorageItem(null, type, null, path);
    }

    /**
     * 创建一个ObjectStorageItem用于获取或者签名
     *
     * @param path
     * @return
     */
    public static ObjectStorageItem newStorageItemToGet(String resourceName, String path) {
        return new ObjectStorageItem(resourceName, null, null, null, path);
    }

    /**
     * 将一个对象存放到存储环境中，并且返回其对象相对路径
     *
     * @param stream
     * @param object 存储对象信息
     * @return
     */
    @Deprecated
    public String pubObject(InputStream stream, ObjectStorageItem object) {
        return putObject(stream, object);
    }

    public ObjectStorageItem createObject(@NotNull ObjectStorageItem object) {
        Preconditions.checkArgument(object.getId() == null, "id property should be null when creating object!");
        object = preprocess(object);

        object.setState(ObjectStorageItemStates.CREATED);
        return store.store(object);
    }

    /**
     * 创建并存储一个对象, 使用contentProvider函数返回对象的InputStream流
     * @param object
     * @param contentProvider
     * @return
     */
    public ObjectStorageItem createAndPutObject(@NotNull ObjectStorageItem object, @NotNull Supplier<InputStream> contentProvider) {
        ObjectStorageItem item = createObject(object);
        try {
            final InputStream stream = contentProvider.get();
            if (stream != null) {
                Preconditions.checkNotNull(stream);
                return putObjectInternal(stream, item);
            }
            else {
                log.error("contentProvider {} 返回stream为null", contentProvider.getClass().getSimpleName());
                item.setState(ObjectStorageItemStates.FAILED);
                store.store(item);
                throw new NullPointerException("stream");
            }
        }
        catch (Exception e) {
            log.error(object + "对象存储时出错", e);
            item.setState(ObjectStorageItemStates.FAILED);
            store.store(item);
            throw e;
        }
    }

    /**
     * 创建并存储一个对象, 如果该对象已存在, 则返回已有对象
     * @param object
     * @param contentProvider
     * @return
     */
    public ObjectStorageItem createAndPutObjectIfNotExists(@NotNull ObjectStorageItem object, @NotNull Supplier<InputStream> contentProvider) {
        Preconditions.checkArgument(StringUtils.isNotBlank(object.getName()), "name of the ObjectStorageItem is required!");
        Preconditions.checkArgument(StringUtils.isNotBlank(object.getResourceConfig()), "resourceConfig of the ObjectStorageItem is required!");
        return findUniqueItem(object).orElseGet(() -> createAndPutObject(object, contentProvider));
    }

    private Optional<ObjectStorageItem> findUniqueItem(@NotNull ObjectStorageItem object) {
        // 查询前需确定对象类型
        preprocessItemType(object);
        return store.findUniqueItem(object);
    }

    /**
     * 将一个对象存放到存储环境中，并且返回其对象相对路径
     *
     * @param stream
     * @param object 存储对象信息
     * @return
     */
    public String putObject(@NotNull InputStream stream, @NotNull ObjectStorageItem object) {
        object = preprocess(object);
        return putObjectInternal(stream, object).getStoragePath();
    }

    private ObjectStorageItem putObjectInternal(InputStream stream, ObjectStorageItem object) {
        ObjectStorageResourceProperties config = getResourceConfig(object);
        final String storagePath = provider.pubObject(stream, object);
        object.setPath(storagePath);
        if (config.needsStore()) {
            object.setState(ObjectStorageItemStates.STORED);
            object = store.store(object);
        }
        return object;
    }

    /**
     * 不进行任何预处理，直接将一个对象存放到存储环境中，并且返回其对象相对路径。
     * 在进行直接上传前，建议调用preprocess提前进行预处理。
     *
     * @param stream 数据流
     * @param object 存储对象信息
     * @return
     */
    public String putObjectWithoutPreprocess(InputStream stream, ObjectStorageItem object) {
        return putObjectInternal(stream, object).getStoragePath();
    }

    /**
     * 从存储环境中获取一个对象的stream
     *
     * @param object 存储对象信息
     * @return
     * @exception ObjectStorageItemNotFoundException 如果指定的对象不存在
     */
    public InputStream getObject(ObjectStorageItem object) {
        object = preprocess(object);
        return provider.getObject(object);
    }

    /**
     * 从存储环境中获取一个对象的stream
     * @param object
     * @return
     */
    public Optional<InputStream> getObjectOptionalWithoutPreprocess(ObjectStorageItem object) {
        try {
            return Optional.of(provider.getObject(object));
        }
        catch (ObjectStorageItemNotFoundException e) {
            return Optional.empty();
        }
    }

    /**
     * 从存储环境中获取一个对象的stream, 并且直接使用对象路径, 不做任何预处理
     *
     * @param object 存储对象信息
     * @return
     * @exception ObjectStorageItemNotFoundException 如果指定的对象不存在
     */
    public InputStream getObjectWithoutPreprocess(ObjectStorageItem object) {
        return provider.getObject(object);
    }

    /**
     * 对指定的对象生成其访问url
     *
     * @param object 存储对象的信息
     * @return
     */
    public String getObjectAccessUrl(ObjectStorageItem object, ObjectStorageItemAccessOptions options) {
        if (object.isResourceConfigSet()) {
            object = preprocess(object);
        }
        if (!object.isPathSet()) {
            if (!object.isNameSet()) {
                // the parameter is wrong
                return null;
            }
        }

        return provider.getObjectAccessUrl(object, options);
    }

    /**
     * 对指定的对象生成其访问url
     *
     * @param object 存储对象的信息
     * @return
     */
    public String getObjectAccessUrl(ObjectStorageItem object) {
        return this.getObjectAccessUrl(object, new ObjectStorageItemAccessOptions());
    }

    /**
     * 对指定的对象存储路径生成其访问url
     *
     * @param objectPath
     * @return
     */
    public String getObjectAccessUrl(String objectPath) {
        if (StringUtils.isBlank(objectPath)) {
            return objectPath;
        }
        return getObjectAccessUrl(ObjectStorageType.PRIVATE_RESOURCE, objectPath);
    }

    /**
     * 批量为指定数组里的对象列表替换url
     *
     * @param objectPathList
     * @return
     */
    public void replaceObjectAccessUrl(List<String> objectPathList) {
        if (CollectionUtils.isEmpty(objectPathList)) {
            return;
        }
        for (int i = 0; i < objectPathList.size(); i++) {
            objectPathList.set(i, getObjectAccessUrl(objectPathList.get(i)));
        }
    }

    /**
     * 批量为指定数组里的对象列表替换url
     *
     * @param objectPathList
     * @return
     */
    public void replaceObjectAccessUrl(ObjectStorageType type, List<String> objectPathList) {
        if (CollectionUtils.isEmpty(objectPathList)) {
            return;
        }
        for (int i = 0; i < objectPathList.size(); i++) {
            objectPathList.set(i, getObjectAccessUrl(type, objectPathList.get(i)));
        }
    }

    /**
     * 对指定的对象存储路径生成其访问url
     *
     * @param objectPath
     * @return
     */
    public String getObjectAccessUrl(ObjectStorageType type, String objectPath) {
        return getObjectAccessUrl(newStorageItemToGet(type, objectPath));
    }

    /**
     * 获取web直传policy
     *
     * @param objectStorageItem
     * @return
     */
    public Map<String, String> getDirectUploadingPolicy(ObjectStorageItem objectStorageItem) {
        ObjectStorageResourceProperties config = objectStorageConfigService.configProperties.getResourceConfig(objectStorageItem.getResourceConfig());

        preprocess(objectStorageItem);

        String storageItemPath = objectStorageItem.getPath();
        Map<String, String> policy = provider.getDirectUploadingPolicy(objectStorageItem, FilenameUtils.getPathNoEndSeparator(storageItemPath));
        policy.put(UploadingPolicyKeys.PATH, storageItemPath);
        return policy;
    }

    public void preprocessItemType(@NotNull ObjectStorageItem object) {
        objectStorageConfigService.preprocessItemType(object);
    }

    public ObjectStorageItem preprocess(ObjectStorageItem objectStorageItem) {
        return objectStorageConfigService.preprocess(objectStorageItem);
    }

    public ObjectStorageResourceProperties getResourceConfig(ObjectStorageItem objectStorageItem) {
        return objectStorageConfigService.getResourceConfig(objectStorageItem);
    }

    /**
     * 判断文件是否存在
     * @param object
     * @return
     */
    public Boolean isObjectExist(ObjectStorageItem object){
        object = preprocess(object);
        return provider.doesObjectExist(object);
    }

    /**
     * 根据指定的资源定义名称, 获取其配置的ObjectStorageType
     * @param resourceName
     * @return
     */
    public ObjectStorageType getResourceStorageType(String resourceName) {
        return objectStorageConfigService.getResourceConfig(resourceName).getStorageType();
    }

    public void delete(ObjectStorageItem objectStorageItem) {
        if (isObjectExist(objectStorageItem)) {
            provider.delete(objectStorageItem);
        }
    }

    /**
     * 获取资源文件的最后修改时间
     * @param object
     * @return
     */
    public Instant getLastModifiedDate(ObjectStorageItem object) {
        return provider.getLastModifiedDate(object);
    }

    /**
     * 追加存储，并且返回其对象相对路径
     *
     * @param bytes
     * @param object 存储对象信息
     * @return
     */
    public Long appendObject(@NotNull ObjectStorageItem object, byte[] bytes, Long position) {
        object = preprocess(object);

        ObjectStorageResourceProperties config = getResourceConfig(object);

        // 追加存储
        ObjectAppendResult objectAppendResult = provider.appendObject(object, bytes, position);
        object.setPath(objectAppendResult.getPath());
        if (config.needsStore()) {
            object.setState(ObjectStorageItemStates.STORED);
            object = store.store(object);
        }
        return objectAppendResult.getNextPosition();
    }

    /**
     * 获取临时访问凭证
     * @param resourceName
     * @return
     */
    public Map<String, String> getTemporaryAccessToken(String resourceName) {
        ObjectStorageResourceProperties config = objectStorageConfigService.configProperties.getResourceConfig(resourceName);
        return provider.getTemporaryAccessToken(config);
    }
}
