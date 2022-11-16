package com.wingedtech.common.log.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

/**
 * 通用操作日志记录
 */
@Data
public class OperationLogDTO implements Serializable {

    private static final long serialVersionUID = 7832756276832047550L;

    private String id;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Instant createdTime;

    /**
     * 客户端操作系统
     */
    private String osName;

    /**
     * 客户端操作系统版本
     */
    private String osVersion;

    /**
     * 客户端浏览器的版本类型
     */
    private String userAgent;

    /**
     * 请求ip
     */
    private String requestIp;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求方式
     */
    private String requestType;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 请求执行的类路径
     */
    private String classPath;

    /**
     * 请求执行的方法名
     */
    private String methodName;

    /**
     * 请求执行的方法描述
     */
    private String description;

    /**
     * 请求开始时间
     */
    private Instant startTime;

    /**
     * 请求结束时间
     */
    private Instant endTime;

    /**
     * 请求时长(毫秒)
     */
    private Long duration;

    /**
     * 请求返回信息
     */
    private String responseData;

    /**
     * http请求状态
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String error;
}
