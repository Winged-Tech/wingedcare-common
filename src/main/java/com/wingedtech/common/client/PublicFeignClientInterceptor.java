package com.wingedtech.common.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class PublicFeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String BEARER_TOKEN_TYPE = "Bearer";

    @Override
    public void apply(RequestTemplate template) {

    }
}
