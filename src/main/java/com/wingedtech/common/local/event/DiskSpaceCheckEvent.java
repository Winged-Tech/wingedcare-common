package com.wingedtech.common.local.event;

import com.wingedtech.common.local.service.dto.DiskSpaceStatusDTO;
import org.springframework.context.ApplicationEvent;

import javax.validation.constraints.NotNull;

/**
 * 磁盘空间检查事件
 *
 * @author 6688SUN
 */
public class DiskSpaceCheckEvent extends ApplicationEvent {

    private final DiskSpaceStatusDTO status;


    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public DiskSpaceCheckEvent(Object source, @NotNull DiskSpaceStatusDTO status) {
        super(source);
        this.status = status;
    }

    public DiskSpaceStatusDTO getStatus() {
        return status;
    }
}
