package com.wingedtech.common.local.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 默认单位G
 *
 * @author 6688SUN
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class DiskSpaceStatusDTO {
    /**
     * 未分配的G
     */
    private Double freeSpace;

    /**
     * 已分配的G
     */
    private Double useSpace;

    /**
     * 总的剩余空间低于多少G
     */
    private Long lessThanOfSpaceAlarm;
}
