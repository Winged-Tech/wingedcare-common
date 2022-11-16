package com.wingedtech.common.local.application;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author 6688SUN
 */
@Data
public class DiskSpaceCheckCommand {
    /**
     * 磁盘目录
     */
    private String path;

    /**
     * 业务对象
     */
    @NotBlank
    private String businessKey;

    /**
     * 多少条数据，默认是1
     */
    private Long dataNumber;
}
