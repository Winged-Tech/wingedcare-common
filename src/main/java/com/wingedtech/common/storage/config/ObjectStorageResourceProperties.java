package com.wingedtech.common.storage.config;

import com.wingedtech.common.storage.ObjectStorageType;
import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;

@Data
public class ObjectStorageResourceProperties {
    /**
     * 资源配置的名称
     */
    private String resourceName;
    /**
     * 该资源配置的统一资源路径前缀（一般为存放的根目录）
     */
    private String prefix;
    /**
     * 如果true，对象的object id会被拼接到最终的路径中
     */
    private Boolean appendObjectId = false;
    /**
     * 如果true，对象最终将以原始文件名进行存储
     * 如果false，对象在存储时会生成随机的唯一文件名
     */
    private Boolean keepOriginalFileName = false;
    /**
     * 资源路径所使用的分隔符
     */
    private String pathSeparator = "/";

    /**
     * 使用的存储类型
     */
    private ObjectStorageType storageType;

    /**
     * 客户端直传完成后的回调地址
     */
    private String callbackUrl;
    /**
     * 如果true, 对象将在数据库内生成一条存储记录
     */
    private Boolean store;

    /**
     * 是否对该资源做多租户处理, 当全局多租户开启时, 默认所有资源文件都将做多租户处理
     */
    private Boolean multiTenant;
    /**
     * 是否需要对资源的文件名拼接时间戳,仅在保持原文件名开启时生效
     */
    private Boolean appendTimestampInFileName = false;


    public boolean needsStore() {
        return BooleanUtils.isTrue(store);
    }

    public boolean needsMultiTenant() {
        return BooleanUtils.isNotFalse(multiTenant);
    }
}
