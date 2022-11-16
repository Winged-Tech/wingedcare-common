package com.wingedtech.common.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class GenericTimerJobVariableDTO implements Serializable {
    private static final long serialVersionUID = 3296787260588422166L;

    /**
     * 通用定时流程变量
     */
    public static final String VARIABLE_NAME = "timerJob";
    /**
     * 定时消息流程 processId
     */
    public static final String DEFINITIONS_KEY = "global-timer-job";

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 开始时间 使用的格式是ISO 8601格式
     */
    private String startTime;

    /**
     * 是否需要重复周期
     */
    private Boolean needCycleTimer;

    /**
     * 持续时间 使用的格式是ISO 8601格式
     */
    private String durationTime;

}
