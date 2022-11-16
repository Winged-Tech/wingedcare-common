package com.wingedtech.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * FeignClient相关工具类
 */
public class FeignClientUtils {
    private static final Logger log = LoggerFactory.getLogger(FeignClientUtils.class);

    /**
     * 解析指定的ResponseEntity结果，并返回其body对象。如果ResponseEntity失败，打印错误日志并返回空。
     * @param result ResponseEntity结果对象
     * @param <T> ResponseEntity里的body类型
     * @return 如果是成功的ResponseEntity，返回其body对象，否则返回null
     */
    public static <T> T parseResponseEntity(ResponseEntity<T> result) {
        if (result.getStatusCode().is2xxSuccessful()) {
            return result.getBody();
        } else {
            log.error("Feign client request failed：{}", result);
            return null;
        }
    }
}
