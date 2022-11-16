package com.wingedtech.common.message.dto;

import lombok.Data;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO description
 *
 * @author Jason
 * @since 2019-06-18 17:06
 */
@Data
public class TemplateMessageDTO implements Serializable {

    private static final long serialVersionUID = 746288043895681745L;

    private String id;

    /**
     * 业务场景
     */
    private String businessKey;

    /**
     * 用于短信接收 value : 手机号
     */
    private String mobile;

    /**
     * 参数
     */
    private Map<String, String> parameters;

    /**
     * 消息由哪个服务生成
     */
    private String producerService;

    /**
     * 延时消息设置
     */
    private DelaySendDTO delayMessage;

    public void addParameter(String parameterName, String parameterValue) {
        if (parameters == null) {
            parameters = new HashMap<>(16);
        }
        parameters.put(parameterName, parameterValue);
    }


    public String getParameter(String parameterName) {
        return MapUtils.getString(parameters, parameterName, null);
    }
}
