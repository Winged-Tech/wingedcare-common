package com.wingedtech.common.security.http;

public class HttpRequestSecurityConfigurationException extends RuntimeException {
    public HttpRequestSecurityConfigurationException() {
    }

    public HttpRequestSecurityConfigurationException(String message) {
        super(message);
    }

    public HttpRequestSecurityConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
