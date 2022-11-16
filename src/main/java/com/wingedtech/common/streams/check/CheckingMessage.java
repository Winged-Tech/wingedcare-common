package com.wingedtech.common.streams.check;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
public class CheckingMessage implements Serializable {

    /**
     * 来源服务实例名
     */
    private String instance;

    /**
     * 来源服务名
     */
    private String sourceService;

    /**
     * 消息创建时间
     */
    private Instant createdTime;

    /**
     * 自定义消息内容
     */
    private String message;

    /**
     * 来源服务主机名
     */
    private String host;
}
