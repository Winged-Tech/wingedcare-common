package com.wingedtech.common.errors;

import org.apache.commons.lang3.StringUtils;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 业务逻辑异常，通常在Service层抛出
 */
public class BusinessException extends AbstractThrowableProblem {
    public static final String DEFAULT_BUSINESS_ERROR_CODE = "BUSINESS_ERROR";
    /**
     * 当触发BusinessException时再HTTP响应头部里添加的header
     */
    public static final String HTTP_HEADER = "W-Business-Error";

    private Serializable data;

    private String code;

    public BusinessException(String detail) {
        this(DEFAULT_BUSINESS_ERROR_CODE, detail, null, null);
    }

    /**
     * 构造一个BusinessException的实例
     * @param businessErrorCode 由业务服务自定义的错误码
     * @param detail 可展示给最终用户阅读的业务逻辑错误信息
     */
    public BusinessException(String businessErrorCode, String detail) {
        this(businessErrorCode, detail, null, null);
    }

    /**
     * 构造一个BusinessException的实例
     * @param businessErrorCode 由业务服务自定义的错误码
     * @param detail 可展示给最终用户阅读的业务逻辑错误信息
     * @param status 自定义的HTTP状态
     */
    public BusinessException(String businessErrorCode, String detail, Status status) {
        this(businessErrorCode, detail, null, null, status);
    }

    /**
     * 构造一个BusinessException的实例
     * @param businessErrorCode 由业务服务自定义的错误码
     * @param detail 可展示给最终用户阅读的业务逻辑错误信息
     * @param data 跟业务逻辑相关的必要数据
     */
    public BusinessException(String businessErrorCode, String detail, Serializable data) {
        this(businessErrorCode, detail, null, data);
    }

    /**
     * 构造一个BusinessException的实例
     * @param businessErrorCode 由业务服务自定义的错误码
     * @param detail 可展示给最终用户阅读的业务逻辑错误信息
     * @param title 错误相关标题
     * @param data 跟业务逻辑相关的必要数据
     */
    public BusinessException(String businessErrorCode, String detail, String title, Serializable data) {
        this(businessErrorCode, detail, title, data, Status.BAD_REQUEST);
    }

    /**
     * 构造一个BusinessException的实例
     * @param businessErrorCode 由业务服务自定义的错误码
     * @param detail 可展示给最终用户阅读的业务逻辑错误信息
     * @param title 错误相关标题
     * @param data 跟业务逻辑相关的必要数据
     * @param status 自定义的HTTP状态
     */
    public BusinessException(String businessErrorCode, String detail, String title, Serializable data, Status status) {
        super(null, title, status, detail, null, null, buildParameters(businessErrorCode, data));
        this.code = businessErrorCode;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public static boolean isBusinessExceptionHeader(String header) {
        return HTTP_HEADER.equals(header);
    }

    private static Map<String, Object> buildParameters(String message, Serializable data) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(message)) {
            map.put("message", message);
        }
        if (data != null) {
            map.put("data", data);
        }
        return map;
    }
}
