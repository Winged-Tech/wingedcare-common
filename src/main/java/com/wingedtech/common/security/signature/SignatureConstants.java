package com.wingedtech.common.security.signature;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 签名通用常量
 *
 * @author 6688 Sun
 */
public final class SignatureConstants {

    /**
     * 签名算法 - config map
     */
    public static final String HMAC = "HMAC";

    /**
     * 固定的 SignatureToken - config map
     */
    public static final String FIXED_SIGNATURE_TOKEN = "fixedSignatureToken";

    /**
     * 对应的真实签名算法名称
     */
    public static final String HMAC_SHA1 = "HmacSHA1";

    public static final String SIGN_FIELD = "sign";

    /**
     * 默认字符集编码
     */
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private SignatureConstants() {

    }
}
