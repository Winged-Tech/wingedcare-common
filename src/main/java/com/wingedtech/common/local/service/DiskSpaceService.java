package com.wingedtech.common.local.service;

import com.wingedtech.common.local.application.DiskSpaceCheckCommand;
import com.wingedtech.common.local.service.dto.DiskSpaceStatusDTO;

/**
 * @author 6688SUN
 */
public interface DiskSpaceService {

    /**
     * 检测磁盘存储状态
     *
     * @param path 存储目录
     * @return 磁盘空间状态
     */
    DiskSpaceStatusDTO check(String path);

    /**
     * 目标磁盘空间是否够用
     *
     * @param command 检查参数
     * @return false：目标磁盘空间不够
     */
    boolean availableTargetDiskSpace(DiskSpaceCheckCommand command);
}
