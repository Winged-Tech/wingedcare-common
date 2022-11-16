package com.wingedtech.common.storage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wingedtech.common.service.beloging.AbstractAuditingEntityDTOWithUserId;
import com.wingedtech.common.storage.config.ObjectStorageResourceConfigProperties;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 对象存储的基本信息
 * @author taozhou
 */
@Data
@ToString(callSuper = true)
public class ObjectStorageItem extends AbstractAuditingEntityDTOWithUserId {
    private static final String DEFAULT_RESOURCE_CONFIG = ObjectStorageResourceConfigProperties.RESOURCE_CONFIG_DEFAULT;

    private String id;
    /**

     * 该对象使用的resource配置，需要在application.yml中预先配置
     */
    private String resourceConfig;
    /**
     * 关联的业务对象id，该id仅用于拼接最终的存储路径，并不做其他形式的存储。
     */
    private String objectId;
    /**
     * 对象类型
     */
    private ObjectStorageType type;
    /**
     * 对象名，一般可以是一个文件名，或者带有相对路径的文件名
     */
    private String name;
    /**
     * 对象存储完整路径（相对路径）
     */
    private String path;
    /**
     * 对象存储状态
     */
    private ObjectStorageItemStates state;

    public ObjectStorageItem() {
    }

    /**
     * 构造一个ObjectStorageItem（path为空）
     * @param type
     * @param name
     */
    public ObjectStorageItem(ObjectStorageType type, String name) {
        this(DEFAULT_RESOURCE_CONFIG, null, type, name, null);
    }

    /**
     * 构造一个ObjectStorageItem（path为空）
     * @param type
     * @param name
     */
    public ObjectStorageItem(String resourceConfig, ObjectStorageType type, String name) {
        this(resourceConfig, null, type, name, null);
    }

    /**
     * 构造一个ObjectStorageItem并已知其存储path
     * @param resourceConfig
     * @param type
     * @param path
     */
    public ObjectStorageItem(String resourceConfig, ObjectStorageType type, String name, String path) {
        this(resourceConfig, null, type, name, path);
    }

    /**
     * 构造一个ObjectStorageItem（path为空）
     * @param resourceConfig
     * @param objectId
     * @param type
     * @param name
     */
    public ObjectStorageItem(String resourceConfig, String objectId, ObjectStorageType type, String name) {
        this(resourceConfig, objectId, type, name, null);
    }

    /**
     * 构造一个ObjectStorageItem
     * @param resourceConfig
     * @param objectId
     * @param type
     * @param name
     * @param path
     */
    public ObjectStorageItem(String resourceConfig, String objectId, ObjectStorageType type, String name, String path) {
        this.resourceConfig = resourceConfig;
        this.objectId = objectId;
        this.type = type;
        this.name = name;
        this.path = path;
    }


    /**
     * 创建一个用于存储的对象
     * @param resourceName
     * @param name
     * @return
     */
    public static ObjectStorageItem put(@NotBlank String resourceName, @NotBlank String name) {
        return put(resourceName, null, name);
    }

    /**
     * 创建一个用于存储的对象
     * @param resourceName
     * @param objectId
     * @param name
     * @return
     */
    public static ObjectStorageItem put(@NotBlank String resourceName, String objectId, String name) {
        return new ObjectStorageItem(resourceName, objectId, null, name);
    }

    /**
     * 创建一个用于获取的对象
     * @param resourceName
     * @param path
     * @return
     */
    public static ObjectStorageItem get(@NotBlank String resourceName, @NotBlank String path) {
        return new ObjectStorageItem(resourceName, null, null, null, path);
    }

    /**
     * 使用最终存储路径获取一个对象
     * @param type
     * @param storagePath
     * @return
     */
    public static ObjectStorageItem get(@NotNull ObjectStorageType type, @NotBlank String storagePath) {
        return new ObjectStorageItem(null, null, type, null, storagePath);
    }

    /**
     * 获取对象最终存储的完整路径（一般为相对路径）
     * @return
     */
    public String getStoragePath() {
        return StringUtils.isNotBlank(path) ? path : name;
    }

    public boolean isResourceConfigSet() {
        return StringUtils.isNotBlank(resourceConfig);
    }

    public boolean isPathSet() {
        return StringUtils.isNotBlank(path);
    }

    public boolean isNameSet() { return StringUtils.isNotBlank(name); }

    public boolean isStorageTypeSet() {
        return this.type != null;
    }

    /**
     * 获取当前对象是否为已存储状态
     * @return
     */
    @JsonIgnore
    public boolean isInStorage() {
        return ObjectStorageItemStates.STORED.equals(state);
    }
}
