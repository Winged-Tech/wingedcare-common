package com.wingedtech.common.security.signature;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

/**
 * @author 6688 Sun
 */
@Slf4j
public class ApiSigner {

    /**
     * 获取待签名字符串内容
     *
     * @param params 请求参数
     */
    public static String getSignCheckContent(Map<String, String> params) {
        if (params == null) {
            return null;
        }

        StringBuilder content = new StringBuilder();
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            content.append(i == 0 ? "" : "&").append(key).append("=").append(value);
        }
        return content.toString();
    }

    /**
     * 验证签名
     *
     * @param content         待验签的内容
     * @param sign            签名值的Base64串
     * @param accessKeySecret 加密签名字符串和服务器端验证签名字符串的密钥
     * @return true：验证成功；false：验证失败
     */
    public static boolean verify(String content, String sign, String accessKeySecret) {
        try {
            final Mac mac = Mac.getInstance(SignatureConstants.HMAC_SHA1);
            mac.init(new SecretKeySpec(accessKeySecret.getBytes(SignatureConstants.DEFAULT_CHARSET), SignatureConstants.HMAC_SHA1));
            final byte[] signData = mac.doFinal(content.getBytes(SignatureConstants.DEFAULT_CHARSET));
            final String currentSign = Base64.getEncoder().encodeToString(signData);
            return StringUtils.equals(currentSign, sign);
        } catch (Exception e) {
            String errorMessage = "验签遭遇异常，content=" + content + " sign=" + sign +
                " accessKeySecret=" + accessKeySecret + " reason=" + e.getMessage();
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * 计算签名
     *
     * @param content         待签名的内容
     * @param accessKeySecret 加密签名字符串和服务器端验证签名字符串的密钥
     * @return 签名值的Base64串
     */
    public String sign(String content, String accessKeySecret) {
        try {
            final Mac mac = Mac.getInstance(SignatureConstants.HMAC_SHA1);
            mac.init(new SecretKeySpec(accessKeySecret.getBytes(SignatureConstants.DEFAULT_CHARSET), SignatureConstants.HMAC_SHA1));
            final byte[] signData = mac.doFinal(content.getBytes(SignatureConstants.DEFAULT_CHARSET));
            return Base64.getEncoder().encodeToString(signData);
        } catch (Exception e) {
            String errorMessage = "签名遭遇异常，content=" + content + " accessKeySecretSize=" + accessKeySecret.length() + " reason=" + e.getMessage();
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * 对参数集合进行验签
     *
     * @param parameters      参数集合
     * @param accessKeySecret 加密签名字符串和服务器端验证签名字符串的密钥
     * @return true：验证成功；false：验证失败
     */
    public static boolean verifyParams(Map<String, String> parameters, String accessKeySecret) {
        String sign = parameters.get(SignatureConstants.SIGN_FIELD);
        parameters.remove(SignatureConstants.SIGN_FIELD);
        String content = getSignCheckContent(parameters);
        return verify(content, sign, accessKeySecret);
    }
}
