package com.wingedtech.common.security.signature;

import com.wingedtech.common.autoconfigure.security.SignatureConfiguration;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author 6688 Sun
 */
public class ApiSignerUtil {
    private static final String ENCODING = "UTF-8";

    public static String getContent(HttpServletRequest request, SignatureConfiguration signatureConfiguration) throws UnsupportedEncodingException {
        // 构造 stringToSign 字符串
        Map parameters = new HashMap();
        final SignatureConfiguration.Header header = signatureConfiguration.getHeader();
        final String signMethodHeader = header.getSignatureMethod();
        final String signVersionHeader = header.getSignatureVersion();
        parameters.put("SignatureMethod", request.getHeader(signMethodHeader) == null ? signatureConfiguration.getConfig().get(SignatureConstants.HMAC) : request.getHeader(signMethodHeader));
        parameters.put("SignatureVersion", request.getHeader(signVersionHeader) == null ? "1.0" : request.getHeader(signVersionHeader));

        // 构造 stringToSign 字符串
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append(request.getMethod()).append("\n");
        stringToSign.append(request.getRequestURI()).append("\n");

        final String signTimestamp = request.getHeader(header.getSignatureTimestamp());
        final String signNonce = request.getHeader(header.getSignatureNonce());
        if (signTimestamp == null) {
            throw new IllegalStateException("Header " + header.getSignatureTimestamp() + " can not empty!");
        }
        if (signNonce == null) {
            throw new IllegalStateException("Header " + header.getSignatureNonce() + " can not empty!");
        }
        stringToSign.append(signTimestamp).append("\n");
        stringToSign.append(signNonce).append("\n");

        // 排序请求参数
        final Set<String> set = parameters.keySet();
        String[] sortedKeys = set.toArray(new String[set.size()]);

        StringBuilder canonicalizedQueryString = new StringBuilder();
        for (String key : sortedKeys) {
            // 这里注意编码 key 和 value
            canonicalizedQueryString.append("&")
                .append(percentEncode(key)).append("=")
                .append(percentEncode(parameters.get(key).toString()));
        }
        // 这里注意编码 canonicalizedQueryString
        final StringBuilder sign = stringToSign.append(percentEncode(canonicalizedQueryString.toString().substring(1)));
        return sign.toString();
    }

    private static String percentEncode(String value) throws UnsupportedEncodingException {
        return value != null ? URLEncoder.encode(value, ENCODING).replace("+", "%20").replace("*", "%2A").replace("%7E", "~") : null;
    }
}
