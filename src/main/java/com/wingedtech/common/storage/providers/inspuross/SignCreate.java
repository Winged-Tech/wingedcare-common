package com.wingedtech.common.storage.providers.inspuross;



import com.aliyun.oss.OSSException;
import com.aliyun.oss.internal.OSSHeaders;
import org.apache.http.HttpHeaders;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.wingedtech.common.storage.providers.inspuross.RequestParameters.*;

/**
 * @author wangdashuai
 * Title: sign.SignCreate
 * ProjectName s3test
 * Description: todo
 * @date 2020/12/16  18:08
 */
class SignCreate {

//    public static void main(String[] args) {
//        Credentials credentials=new Credentials("YWM4MzRhNTItMDUzMi00MWU0LWI5YzUtOTUxMTU0ZmFjMGE0","MDJiOWJhOGUtOGJkZC00NWRjLTlhZjctMTkyZDYyNWNmZjQz");
//        String bucketName="gtsms";
//        String key="ucas/rawdata/null/1608112664670-微信图片_20191211142141.gif";
//        Long contentLength=60316L;
//        String date="Wed, 16 Dec 2020 09:41:07 GMT";
//        OSSRequest ossRequest =new OSSRequest(bucketName,key,credentials, HttpMethod.PUT,"oss.cn-north-3.inspurcloudoss.com");
//        ossRequest.setContentLength(contentLength);
//        System.out.println(addCommonHeader(ossRequest,date));
//
//    }
    public static final String NEW_LINE = "\n";
    public static final List<String> SIGNED_PARAMTERS = Arrays.asList(new String[]{SUBRESOURCE_ACL,
            SUBRESOURCE_UPLOADS, SUBRESOURCE_LOCATION, SUBRESOURCE_CORS, SUBRESOURCE_LOGGING, SUBRESOURCE_WEBSITE,
            SUBRESOURCE_REFERER, SUBRESOURCE_STAT, SUBRESOURCE_UDF, SUBRESOURCE_UDF_NAME,
            SUBRESOURCE_UDF_IMAGE, SUBRESOURCE_UDF_IMAGE_DESC, SUBRESOURCE_UDF_APPLICATION, SUBRESOURCE_UDF_LOG,
            SUBRESOURCE_RESTORE, SUBRESOURCE_STYLE, SUBRESOURCE_VRESIONS, SUBRESOURCE_VRESIONING, SUBRESOURCE_VRESION_ID,
            SUBRESOURCE_ENCRYPTION, SUBRESOURCE_POLICY, SUBRESOURCE_REQUEST_PAYMENT, SUBRESOURCE_BUCKET_INFO, SUBRESOURCE_COMP,
            SUBRESOURCE_QOS, SUBRESOURCE_LIVE, SUBRESOURCE_STATUS, SUBRESOURCE_REPLICATION_PROGRESS, SUBRESOURCE_REPLICATION_LOCATION,
            SUBRESOURCE_CNAME, SUBRESOURCE_START_TIME, SUBRESOURCE_END_TIME,
            SUBRESOURCE_PROCESS_CONF, SUBRESOURCE_VOD, SUBRESOURCE_SYMLINK, SUBRESOURCE_LIFECYCLE, SUBRESOURCE_DELETE,
            SUBRESOURCE_TAGGING, SUBRESOURCE_OBJECTMETA, SUBRESOURCE_IMG,
            SECURITY_TOKEN, UPLOAD_ID, PART_NUMBER, RESPONSE_HEADER_CACHE_CONTROL,
            RESPONSE_HEADER_CONTENT_DISPOSITION, RESPONSE_HEADER_CONTENT_ENCODING, RESPONSE_HEADER_CONTENT_LANGUAGE,
            RESPONSE_HEADER_CONTENT_TYPE, RESPONSE_HEADER_EXPIRES, STYLE_NAME, OSS_TRAFFIC_LIMIT, BUCKET_DOMAIN, NOTIFICATION});

    public static String addCommonHeader(OSSRequest ossRequest, String date) {
        Map<String, String> headers = ossRequest.getHeaders();
        if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            headers.put(HttpHeaders.CONTENT_TYPE, "application/xml");
        }
        headers.put("User-Agent", "PostmanRuntime/7.26.8");
        if (ossRequest.getContentLength() != null) {
            headers.put(HttpHeaders.CONTENT_LENGTH, ossRequest.getContentLength().toString());
        }
        if (ossRequest.getContentMD5() != null) {
            headers.put(HttpHeaders.CONTENT_MD5, ossRequest.getContentMD5());
        }
        headers.put(HttpHeaders.CONNECTION, "keep-alive");
        headers.put(HttpHeaders.DATE, date);
        return  getSign(ossRequest);



    }

    public static String getSign(OSSRequest ossRequest) {
        Credentials credentials = ossRequest.getCredentials();
        String accessKey = credentials.getAccessKey();
        String secretKey = credentials.getSecretAccessKey();
        String canonicalString = buildCanonicalString(ossRequest);
        return "OSS " + accessKey + ":" + signWithHmacSha1(secretKey, canonicalString);

    }

    public static String signWithHmacSha1(String secretKey, String canonicalString) {

        try {
            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            return Base64.getEncoder().encodeToString(mac.doFinal(canonicalString.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
            throw new OSSException(e.getMessage(), e);
        }

    }

    public static String buildCanonicalString(OSSRequest ossRequest) {

        StringBuilder canonicalString = new StringBuilder();
        canonicalString.append(ossRequest.getMethod().toString()).append(NEW_LINE);

        Map<String, String> headers = ossRequest.getHeaders();
        TreeMap<String, String> headersToSign = new TreeMap<String, String>();

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (header.getKey() == null) {
                    continue;
                }

                String lowerKey = header.getKey().toLowerCase();
                if (lowerKey.equals(HttpHeaders.CONTENT_TYPE.toLowerCase())
                        || lowerKey.equals(HttpHeaders.CONTENT_MD5.toLowerCase())
                        || lowerKey.equals(HttpHeaders.DATE.toLowerCase())
                        || lowerKey.startsWith(OSSHeaders.OSS_PREFIX)) {
                    headersToSign.put(lowerKey, header.getValue().trim());
                }
            }
        }

        if (!headersToSign.containsKey(HttpHeaders.CONTENT_TYPE.toLowerCase())) {
            headersToSign.put(HttpHeaders.CONTENT_TYPE.toLowerCase(), "");
        }
        if (!headersToSign.containsKey(HttpHeaders.CONTENT_MD5.toLowerCase())) {
            headersToSign.put(HttpHeaders.CONTENT_MD5.toLowerCase(), "");
        }

        for (Map.Entry<String, String> entry : headersToSign.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key.startsWith(OSSHeaders.OSS_PREFIX)) {
                canonicalString.append(key).append(':').append(value);
            } else {
                canonicalString.append(value);
            }

            canonicalString.append(NEW_LINE);
        }

        canonicalString.append(buildCanonicalizedResource(ossRequest.getResourcePath(), ossRequest.getParameters()));

        return canonicalString.toString();
    }

    public static String buildCanonicalizedResource(String resourcePath, Map<String, String> parameters) {
        if (!resourcePath.startsWith("/")) {
            throw new OSSException("resourcePath should start with /");
        }
        StringBuilder builder = new StringBuilder();
        builder.append(resourcePath);
        if (parameters != null) {
            String[] parameterNames = parameters.keySet().toArray(new String[parameters.size()]);
            Arrays.sort(parameterNames);
            char separator = '?';
            for (String paramName : parameterNames) {
                if (!SIGNED_PARAMTERS.contains(paramName)) {
                    continue;
                }
                builder.append(separator).append(paramName);
                String paramValue = parameters.get(paramName);
                if (paramValue != null) {
                    builder.append("=").append(paramValue);
                }
                separator = '&';
            }
        }

        return builder.toString();
    }


}
