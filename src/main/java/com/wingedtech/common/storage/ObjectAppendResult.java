package com.wingedtech.common.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 追加上传文件结果
 *
 * @author zhangyp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectAppendResult implements Serializable {
    private static final long serialVersionUID = -1868287394360063494L;

    /**
     * 对象存储完整路径（相对路径）
     */
    private String path;

    /**
     * 下次追加位置
     */
    private Long nextPosition;
}
