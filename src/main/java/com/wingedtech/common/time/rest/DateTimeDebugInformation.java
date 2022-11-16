package com.wingedtech.common.time.rest;

import com.wingedtech.common.time.DateTimeUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;

/**
 * 当前系统时间debug信息
 */
@Getter
@Setter
public class DateTimeDebugInformation {

    @ApiModelProperty("当前时间debug是否已开启")
    private boolean isDebugOn;

    @ApiModelProperty("当前时间debug偏移量")
    private Duration currentDebugTimeOffset;

    @ApiModelProperty("当前系统时间")
    private Instant currentTime;

    public DateTimeDebugInformation() {
        this.isDebugOn = DateTimeUtils.isDebugClockOn();
        this.currentDebugTimeOffset = DateTimeUtils.getDebugTimeOffset();
        this.currentTime = DateTimeUtils.nowInstant();
    }
}
