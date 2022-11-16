package com.wingedtech.common.storage.providers.inspuross;



import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangdashuai
 * Title: OSSRequestMsg
 * ProjectName inspursdkoss
 * Description: todo
 * @date 2019/10/5  10:01
 */
class OSSRequest {
    private String bucketName;
    private String key;
    private String url;
    private Map<String, String> headers = new HashMap();
    private HttpMethod method;
    private Credentials credentials;
    private Map<String, String> parameters;
    private String contentMD5;
    private InputStream content;
    private Long contentLength;

    public String getBucketName() {
        return bucketName;
    }

    public String getKey() {
        return key;
    }


    public String getContentMD5() {
        return contentMD5;
    }

    public void setContentMD5(String contentMD5) {
        this.contentMD5 = contentMD5;
    }

    public OSSRequest(String bucketName, String key, Credentials credentials, HttpMethod method, String endpoint) {
        this.bucketName = bucketName;
        this.key = key;
        this.credentials = credentials;
        this.method = method;
        this.url=endpoint+getResourcePath();
    }

    public String getResourcePath() {
        String resourcePath = "/";
        if (bucketName != null) {
            resourcePath = resourcePath + bucketName;
            if (key != null) {
                resourcePath = resourcePath + "/" + EncodeUtils.urlEncode(key, true);
            }
        }
        return resourcePath;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public InputStream getContent() {
        return content;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }


}
