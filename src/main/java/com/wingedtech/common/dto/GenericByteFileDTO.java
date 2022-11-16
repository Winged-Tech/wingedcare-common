package com.wingedtech.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用 {@link Byte} fileDTO
 *
 * @author Jason
 * @since 2019-04-17 16:11
 */
@Data
public class GenericByteFileDTO implements Serializable {
    private static final long serialVersionUID = -3016632746595254378L;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 数据
     */
    private byte[] data;

    private GenericByteFileDTO(String fileName, byte[] data) {
        this.fileName = fileName;
        this.data = data;
    }

    public static GenericByteFileDTO build(String fileName, byte[] data) {
        return new GenericByteFileDTO(fileName, data);
    }
}
