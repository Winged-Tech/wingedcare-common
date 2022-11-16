package com.wingedtech.common.multitenancy.oauth2;

import com.wingedtech.common.multitenancy.Constants;
import com.wingedtech.common.multitenancy.Tenant;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * 可供RestTemplate使用的ClientHttpRequestInterceptor，为所有请求添加tenant信息header
 */
public class MultiTenantHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        if (Tenant.isCurrentTenantIdSet()) {
            request.getHeaders().add(Constants.HEADER_TENANT_ID, Tenant.getCurrentTenantId());
        }
        return execution.execute(request, body);
    }
}
