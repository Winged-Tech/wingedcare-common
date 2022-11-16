package com.wingedtech.common.client;

import com.wingedtech.common.errors.BusinessException;
import com.wingedtech.common.errors.ResponseUtils;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 从FeignClient中解析BusinessException的ErrorDecoder
 */
@Slf4j
public class BusinessExceptionFeignDecoder implements ErrorDecoder {

    private final ErrorDecoder decoder;

    public BusinessExceptionFeignDecoder(ErrorDecoder decoder) {
        Objects.requireNonNull(decoder, "Decoder must not be null. ");
        this.decoder = decoder;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        // 目前仅支持返回状态码400，并且
        final BusinessException businessException = ResponseUtils.extractBusinessException(response);
        if (businessException != null) {
            return businessException;
        }

        return decoder.decode(methodKey, response);
    }
}
