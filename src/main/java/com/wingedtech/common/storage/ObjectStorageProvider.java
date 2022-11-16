package com.wingedtech.common.storage;

import com.wingedtech.common.storage.config.ObjectStorageResourceProperties;

import java.io.InputStream;
import java.time.Instant;
import java.util.Map;

public interface ObjectStorageProvider {
    /**
     * 将一个对象存放到存储环境中，并且返回其对象相对路径
     * @param stream
     * @param object 存储对象信息
     * @return
     */
    String pubObject(InputStream stream, ObjectStorageItem object);

    /**
     * 从存储环境中获取一个对象的stream
     * @param object 存储对象信息
     * @return
     */
    InputStream getObject(ObjectStorageItem object);

    /**
     * 获取网页直传policy
     * @param object 存储对象信息
     * @return
     */
    Map<String, String> getDirectUploadingPolicy(ObjectStorageItem object, String prefix);

    /**
     * 对指定的对象生成其访问url
     * @param object 存储对象信息
     * @return
     */
    String getObjectAccessUrl(ObjectStorageItem object, ObjectStorageItemAccessOptions options);

    /**
     * 判断文件是否存在
     * @param object
     * @return
     */
    Boolean doesObjectExist(ObjectStorageItem object);

    /**
     * 删除文件
     * @param path
     */
    void delete(ObjectStorageItem path);

    /**
     * 获取文件的最后更新时间
     * @param object
     * @return
     */
    Instant getLastModifiedDate(ObjectStorageItem object);

    /**
     * 追加存储，返回追加结果
     *
     * @param object   存储对象信息
     * @param bytes    追加信息
     * @param position 追加位置
     * @return
     */
    ObjectAppendResult appendObject(ObjectStorageItem object, byte[] bytes, Long position);

    /**
     * 获取临时访问凭证
     * @param resourceConfig
     * @return
     */
    Map<String, String> getTemporaryAccessToken(ObjectStorageResourceProperties resourceConfig);
}
