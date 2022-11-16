package com.wingedtech.common.analytics.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

/**
 * 事件数据
 *
 * @author apple
 */
@Data
public class EventDataDTO implements Serializable {
    private static final long serialVersionUID = 4720364864940018213L;

    /**
     * ID
     */
    private String id;

    /**
     * login
     */
    private String userId;

    /**
     * 创建时间
     */
    private Instant createdTime;

    /**
     * 事件名称
     */
    private String name;

    /**
     * 请求来源IP
     */
    private String requestIp;

    /**
     * 请求URL
     */
    private String requestUrl;
}
