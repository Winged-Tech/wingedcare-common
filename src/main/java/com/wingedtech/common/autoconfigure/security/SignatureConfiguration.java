package com.wingedtech.common.autoconfigure.security;

import com.google.common.collect.Maps;
import com.wingedtech.common.config.Constants;
import com.wingedtech.common.security.signature.SignatureConstants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * @author 6688 Sun
 */
@Configuration
@ConfigurationProperties(value = Constants.CONFIG_SERVICE_PROPERTIES_ROOT + ".signature")
public class SignatureConfiguration {

    /**
     * 加密签名字符串和服务器端验证签名字符串的密钥
     */
    @Getter
    @Setter
    private String accessKeySecret;

    @Getter
    @Setter
    private Map<String, Object> config = Maps.newHashMap(signType());

    /**
     * 自定义签名header
     */
    @Getter
    private Header header = new Header();

    /**
     * 固定验签的api
     */
    @Getter
    @Setter
    private Set<String> fixedApi = new HashSet<>();

    /**
     * 不验签的api
     */
    @Getter
    @Setter
    private Set<String> ignoreApi = new HashSet<>();

    @Getter
    @Setter
    public static class Header {
        /**
         * 签名方式
         */
        private String signatureMethod = "SignatureMethod";
        /**
         * 签名算法版本
         */
        private String signatureVersion = "SignatureVersion";
        /**
         * 唯一随机数，用于防止网络重放攻击
         */
        private String signatureNonce = "SignatureNonce";
        /**
         * 接口调用的时间戳
         */
        private String signatureTimestamp = "SignatureTimestamp";
        /**
         * 签名信息
         */
        private String signature = "Signature";
    }

    private Map<String, String> signType() {
        Map<String, String> init = new HashMap<>();
        init.put(SignatureConstants.HMAC, SignatureConstants.HMAC_SHA1);
        init.put(SignatureConstants.FIXED_SIGNATURE_TOKEN, Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes(SignatureConstants.DEFAULT_CHARSET)));
        return init;
    }
}
