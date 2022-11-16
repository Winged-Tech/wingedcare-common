package com.wingedtech.common.storage.inspur;

import com.inspurcloud.oss.exception.OSSException;
import com.inspurcloud.oss.model.Credentials;
import com.inspurcloud.oss.model.OSSRequest;
import com.inspurcloud.oss.util.SignUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.inspurcloud.oss.util.SignUtil.buildCanonicalString;
import static com.inspurcloud.oss.util.SignUtil.buildCanonicalizedResource;

/**
 * @author taozhou
 * @date 2020/12/22
 */
@Slf4j
public class ModifiedInspurSignUtils {
    public static final String NEW_LINE = "\n";
    public static final List<String> SIGNED_PARAMTERS = SignUtil.SIGNED_PARAMTERS;

    public ModifiedInspurSignUtils() {
    }

    public static Request addCommonHeader(OSSRequest ossRequest, Request request, String signature, String customDate) {
        Map<String, String> headers = ossRequest.getHeaders();
        if (!headers.containsKey("Content-Type")) {
            headers.put("Content-Type", "application/xml");
        }

//        headers.put("User-Agent", "oss-sdk-java/1.2.0");
        if (ossRequest.getContentLength() != null) {
            headers.put("Content-Length", ossRequest.getContentLength().toString());
        }

        if (ossRequest.getContentMD5() != null) {
            headers.put("Content-MD5", ossRequest.getContentMD5());
        }

//        headers.put("Connection", "Keep-Alive");
        if (customDate != null) {
//            headers.put("x-amz-date", customDate);
        }
        else {
            Calendar cd = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String date = sdf.format(cd.getTime());
//            headers.put("x-amz-date", date);
        }
        if (signature != null) {
            String sign = getSign(ossRequest);
            log.info("Java SDK signature (可用于和我们的签名对比) is {}", sign);
//            headers.put("Authorization", signature);
        }
        else {
//            headers.put("Authorization", getSign(ossRequest));
        }
        Request.Builder builder = request.newBuilder();
        Iterator var7 = headers.keySet().iterator();

        while(var7.hasNext()) {
            String key = (String)var7.next();
            builder.addHeader(key, (String)headers.get(key));
        }

        Request requestWithHeaders = builder.build();
        return requestWithHeaders;
    }

    public static String getSign(OSSRequest ossRequest) {
        Credentials credentials = ossRequest.getCredentials();
        String accessKey = credentials.getAccessKey();
        String secretKey = credentials.getSecretAccessKey();
        String canonicalString = buildCanonicalString(ossRequest);
        log.info("Java SDK signing data string: {}", canonicalString);
        return "OSS " + accessKey + ":" + signWithHmacSha1(secretKey, canonicalString);
    }

    public static String signWithHmacSha1(String secretKey, String canonicalString) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            return Base64.getEncoder().encodeToString(mac.doFinal(canonicalString.getBytes("UTF-8")));
        } catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException var4) {
            throw new OSSException(var4.getMessage(), var4);
        }
    }
}
