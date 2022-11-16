package com.wingedtech.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * 通用响应数据结构
 *
 * @param <T>
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericResponseWrapperDTO<T> implements Serializable {

    private static final long serialVersionUID = 20135656192404922L;

    private static final String CODE_SUCCESS = "SUCCESS";
    private static final String CODE_FAIL = "FAIL";

    /**
     * code
     */
    private String code;

    /**
     * detail
     */
    private String detail;

    /**
     * data
     */
    private T data;

    public static <T> GenericResponseWrapperDTO<T> success(String detail, T data) {
        return new GenericResponseWrapperDTO<>(CODE_SUCCESS, detail, data);

    }

    public static <T> GenericResponseWrapperDTO<T> fail(String detail, T data) {
        return new GenericResponseWrapperDTO<>(CODE_FAIL, detail, data);

    }

    public GenericResponseWrapperDTO(String code, String detail, T data) {
        this.code = code;
        this.detail = detail;
        this.data = data;
    }

    public boolean isSuccess() {
        return StringUtils.equals(CODE_SUCCESS, this.code);
    }
}
