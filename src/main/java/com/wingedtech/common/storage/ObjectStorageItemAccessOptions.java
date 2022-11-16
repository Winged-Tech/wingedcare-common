package com.wingedtech.common.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ObjectStorageItem资源访问选项
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObjectStorageItemAccessOptions {
    /**
     * 是否作为文件下载资源
     */
    @Builder.Default
    private boolean download = false;
    /**
     * 资源下载或访问的最终文件名
     */
    private String fileName;
    /**
     * 图片类型资源的访问样式名称
     */
    private String imageStyle;
}
