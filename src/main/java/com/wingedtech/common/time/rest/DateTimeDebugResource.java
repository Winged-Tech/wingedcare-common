package com.wingedtech.common.time.rest;

import com.wingedtech.common.time.DateTimeUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.time.Duration;

@RestController
@RequestMapping("/api/debug/time")
@ConditionalOnProperty("winged.time.debug")
public class DateTimeDebugResource {

    @ApiOperation("检查当前时间日期的debug状态")
    @GetMapping("/check")
    public DateTimeDebugInformation check() {
        return new DateTimeDebugInformation();
    }

    @ApiOperation("开启时间日期的debug模式，为系统时钟设置指定天数的固定偏移量")
    @GetMapping("/offset-days")
    public DateTimeDebugInformation setDebugTimeOffsetDays(@RequestParam int days) {
        return setDebugTimeOffset(Duration.ofDays(days));
    }

    @ApiOperation("开启时间日期的debug模式，为系统时钟设置一个固定的偏移量")
    @GetMapping("/offset")
    public DateTimeDebugInformation setDebugTimeOffset(@NotNull @RequestParam Duration offset) {
        DateTimeUtils.setDebugTimeOffset(offset);
        return check();
    }

    @ApiOperation("关闭时间日期的debug")
    @GetMapping("/off")
    public DateTimeDebugInformation turnOff() {
        DateTimeUtils.removeDebugClock();
        return check();
    }
}
