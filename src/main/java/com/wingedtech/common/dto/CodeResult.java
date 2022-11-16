package com.wingedtech.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

@Data
public class CodeResult implements Serializable {
    private static final long serialVersionUID = -4146321402199177800L;


    /**
     * 验证码操作结果，true为成功
     */
    @ApiModelProperty("验证码操作结果，true为成功")
    private Boolean success;

    /**
     * 详细消息
     */
    @ApiModelProperty("详细消息")
    private String detail;


    /**
     * 附加信息。
     *
     * @return Additional information, or null if none.
     */
    private Map<String, String> additionalInformation = null;

    public void addAdditionalInformation(String key, String value) {
        if (this.additionalInformation == null) {
            this.additionalInformation = new TreeMap<>();
        }

        this.additionalInformation.put(key, value);

    }


    public static CodeResult successful() {
        CodeResult result = new CodeResult();
        result.setSuccess(true);
        return result;
    }

    public static CodeResult failed(String message) {
        CodeResult result = new CodeResult();
        result.setSuccess(false);
        result.setDetail(message);
        return result;
    }
}
