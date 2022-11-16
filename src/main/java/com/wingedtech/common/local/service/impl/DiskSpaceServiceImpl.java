package com.wingedtech.common.local.service.impl;

import com.google.common.base.Strings;
import com.wingedtech.common.local.application.DiskSpaceCheckCommand;
import com.wingedtech.common.local.config.DiskSpaceProperties;
import com.wingedtech.common.local.event.DiskSpaceCheckEvent;
import com.wingedtech.common.local.service.DiskSpaceService;
import com.wingedtech.common.local.service.dto.DiskSpaceStatusDTO;
import com.wingedtech.common.local.types.DiskSpaceStatus;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import javax.annotation.Nullable;
import javax.validation.Valid;

/**
 * @author 6688SUN
 */
public class DiskSpaceServiceImpl implements DiskSpaceService, ApplicationEventPublisherAware {
    private ApplicationEventPublisher applicationEventPublisher;
    private final DiskSpaceProperties diskSpaceProperties;

    public DiskSpaceServiceImpl(DiskSpaceProperties diskSpaceProperties) {
        this.diskSpaceProperties = diskSpaceProperties;
    }

    /**
     * 检测磁盘存储状态
     *
     * @param path 存储目录
     * @return 磁盘空间状态
     */
    @Override
    public DiskSpaceStatusDTO check(@Nullable String path) {
        final DiskSpaceStatus diskSpaceStatus = getDiskSpaceStatus(path);
        DiskSpaceStatusDTO dto = new DiskSpaceStatusDTO();
        dto.setFreeSpace(diskSpaceStatus.getFreeSpaceGb());
        dto.setUseSpace(diskSpaceStatus.getUseSpaceGb());
        if (BooleanUtils.isTrue(diskSpaceProperties.getEnabledEvent())) {
            final long lessThanOfSpaceAlarm = diskSpaceProperties.getLessThanOfSpaceAlarm() == null ? 0 : diskSpaceProperties.getLessThanOfSpaceAlarm();
            dto.setLessThanOfSpaceAlarm(lessThanOfSpaceAlarm);
            applicationEventPublisher.publishEvent(new DiskSpaceCheckEvent(this, dto));
        }
        return dto;
    }

    /**
     * 目标磁盘空间是否够用
     *
     * @param command 检查参数
     * @return false：目标磁盘空间不够
     */
    @Override
    public boolean availableTargetDiskSpace(@Valid DiskSpaceCheckCommand command) {
        final DiskSpaceStatus diskSpaceStatus = getDiskSpaceStatus(command.getPath());
        final long number = command.getDataNumber() == null ? 1 : command.getDataNumber();
        return diskSpaceProperties.getBusinessDataUseSpace().get(command.getBusinessKey()) * number < diskSpaceStatus.getFreeSpace();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    private DiskSpaceStatus getDiskSpaceStatus(String path) {
        return new DiskSpaceStatus(Strings.isNullOrEmpty(path) ? diskSpaceProperties.getGlobalStoragePath() : path);
    }
}
