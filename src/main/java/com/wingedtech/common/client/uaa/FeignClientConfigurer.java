package com.wingedtech.common.client.uaa;

import com.wingedtech.common.client.BusinessExceptionFeignDecoder;
import com.wingedtech.common.multitenancy.oauth2.MultiTenantOAuth2ClientContext;
import com.wingedtech.common.multitenancy.oauth2.MultiTenantOAuth2FeignRequestInterceptor;
import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import io.github.jhipster.security.uaa.LoadBalancedResourceDetails;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;

/**
 * 帮助手动进行FeignClient build的configuration基类
 * FeignClient注解无法支持同一service具有多种不同配置的FeignClient，因此，在必要时需要手动build FeignClient
 * https://github.com/spring-cloud/spring-cloud-netflix/issues/1211
 */
@Import(FeignClientsConfiguration.class)
public class FeignClientConfigurer {

    private Decoder decoder;
    private Encoder encoder;
    private Client client;
    private Contract contract;
    private LoadBalancedResourceDetails loadBalancedResourceDetails;
    private RequestInterceptor internalRequestInterceptor;
    private ErrorDecoder errorDecoder;

    public FeignClientConfigurer(Decoder decoder, Encoder encoder, Client client, Contract contract, LoadBalancedResourceDetails loadBalancedResourceDetails) {
        this(decoder, encoder, client, contract, loadBalancedResourceDetails, new ErrorDecoder.Default());
    }

    public FeignClientConfigurer(Decoder decoder, Encoder encoder, Client client, Contract contract, LoadBalancedResourceDetails loadBalancedResourceDetails, ErrorDecoder errorDecoder) {
        this.decoder = decoder;
        this.encoder = encoder;
        this.client = client;
        this.contract = contract;
        this.errorDecoder = new BusinessExceptionFeignDecoder(errorDecoder);
        this.loadBalancedResourceDetails = loadBalancedResourceDetails;
    }

    /**
     * DefaultOAuth2ClientContext使得Feign Client在调用前能获取到一个默认的"internal"身份（需要额外的oauth2配置），适用于在消息队列触发等场景下的跨服务调用
     * @return
     */
    public RequestInterceptor getOAuth2RequestInterceptor() {
        return getInternalRequestInterceptor();
    }

    private RequestInterceptor getInternalRequestInterceptor() {
        if (internalRequestInterceptor == null) {
            internalRequestInterceptor = new MultiTenantOAuth2FeignRequestInterceptor(new MultiTenantOAuth2ClientContext(), loadBalancedResourceDetails, null);
        }
        return internalRequestInterceptor;
    }

    /**
     * UserFeignClientInterceptor - 由JHipster脚手架生成的能自动在服务间调用时传递当前用户AccessToken的拦截器，当在服务间调用时需要传递用户身份时使用
     * @return
     */
    public RequestInterceptor getUserFeignClientInterceptor() {
        return new UserFeignClientInterceptor();
    }

    /**
     * 为指定的service及接口创建一个AuthorizedFeignClient（使用OAuth2FeignRequestInterceptor拦截器）
     * 此类Client使用internal账号权限进行调用
     * @param clazz
     * @param serviceName
     * @param <T>
     * @return
     */
    public <T> T buildAuthorizedFeignClient(Class<T> clazz, String serviceName) {
        return buildOnTarget(clazz, serviceName, getAuthorizedFeignClientBuilder());
    }

    /**
     * 为指定的service及接口创建一个AuthorizedFeignClient（使用OAuth2FeignRequestInterceptor拦截器）
     * 此类Client使用internal账号权限进行调用
     * @return
     */
    public <T> T buildInternalFeignClient(Class<T> clazz, String serviceName) {
        return buildAuthorizedFeignClient(clazz, serviceName);
    }

    /**
     * 为指定的service及接口创建一个AuthorizedFeignClient（使用OAuth2FeignRequestInterceptor拦截器）
     * 此类Client使用internal账号权限进行调用，并且开启decode404选项
     * @return
     */
    public <T> T buildInternalFeignClientWithDecode404(Class<T> clazz, String serviceName) {
        return buildOnTarget(clazz, serviceName, getAuthorizedFeignClientBuilder().decode404());
    }

    protected Feign.Builder getAuthorizedFeignClientBuilder() {
        return getBasicBuilder().requestInterceptor(getOAuth2RequestInterceptor());
    }

    /**
     * 为指定的service及接口创建一个AuthorizedUserFeignClient（使用UserFeignClientInterceptor拦截器）
     * @param clazz
     * @param serviceName
     * @param <T>
     * @return
     */
    public <T> T buildAuthorizedUserFeignClient(Class<T> clazz, String serviceName) {
        return getAuthorizedUserFeignClientBuilder().target(clazz, buildServiceUrl(serviceName));
    }

    /**
     * 为指定的service及接口创建一个AuthorizedUserFeignClient（使用UserFeignClientInterceptor拦截器）
     * 此类client使用当前登录用户的身份进行调用
     */
    public <T> T buildUserFeignClient(Class<T> clazz, String serviceName) {
        return buildAuthorizedUserFeignClient(clazz, serviceName);
    }

    /**
     * 为指定的service及接口创建一个AuthorizedUserFeignClient（使用UserFeignClientInterceptor拦截器）
     * 此类client使用当前登录用户的身份进行调用，并且开启了decode404选项
     */
    public <T> T buildUserFeignClientWithDecode404(Class<T> clazz, String serviceName) {
        return buildOnTarget(clazz, serviceName, getAuthorizedUserFeignClientBuilder().decode404());
    }

    protected Feign.Builder getAuthorizedUserFeignClientBuilder() {
        return getBasicBuilder().requestInterceptor(getUserFeignClientInterceptor());
    }

    /**
     * 根据指定的builder，构建target
     * @param clazz
     * @param serviceName
     * @param builder
     * @param <T>
     * @return
     */
    public static <T> T buildOnTarget(Class<T> clazz, String serviceName, Feign.Builder builder) {
        return builder.target(clazz, buildServiceUrl(serviceName));
    }

    public static String buildServiceUrl(String serviceName) {
        return "http://" + serviceName;
    }

    /**
     * 获取一个具有默认Encoder、Decoder、Client以及Contract的Feign.Builder
     * @return
     */
    protected Feign.Builder getBasicBuilder() {
        return Feign.builder().client(client).encoder(encoder).decoder(decoder).contract(contract).errorDecoder(errorDecoder);
    }
}
