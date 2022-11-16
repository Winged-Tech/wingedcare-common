package com.wingedtech.common.local.types;

import lombok.Getter;

import javax.validation.ValidationException;
import java.io.File;
import java.math.BigDecimal;

/**
 * 存储状态
 *
 * @author 6688SUN
 */
@Getter
public class DiskSpaceStatus {

    /**
     * 存储路径
     */
    private final String path;

    /**
     * 未分配的字节数
     */
    private final long freeSpace;

    /**
     * 已分配的字节数
     */
    private final long useSpace;

    public DiskSpaceStatus(String path) {
        if (path == null) {
            throw new ValidationException("path不能为空");
        }
        final File file = getFile(path);
        if (!file.isDirectory()) {
            throw new ValidationException("path不是一个目录");
        }
        final long diskFreeSpace = file.getFreeSpace();
        this.path = path;
        this.freeSpace = diskFreeSpace;
        this.useSpace = file.getTotalSpace() - diskFreeSpace;
    }

    public static boolean isValid(String path) {
        File file = getFile(path);
        return file.isDirectory();
    }

    public double getFreeSpaceGb() {
        return new BigDecimal(this.freeSpace / 1024D / 1024D / 1024D).doubleValue();
    }

    public double getFreeSpaceMb() {
        return new BigDecimal(this.freeSpace / 1024D / 1024D).doubleValue();
    }

    public double getUseSpaceGb() {
        return new BigDecimal(this.useSpace / 1024D / 1024D / 1024D).doubleValue();
    }

    public double getUseSpaceMb() {
        return new BigDecimal(this.useSpace / 1024D / 1024D).doubleValue();
    }

    private static File getFile(String path) {
        return new File(path);
    }
}
