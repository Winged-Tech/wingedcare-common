package com.wingedtech.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

@Slf4j
public class RestApiClient {


    public static ResponseEntity<JsonNode> restPost(Map<String, Object> params, String uri, HttpHeaders reqHeaders){
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(params, reqHeaders);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForEntity(uri, entity, JsonNode.class);
    }

    public static ResponseEntity<JsonNode> restGet(Map<String, Object> params, String uri, HttpHeaders reqHeaders){
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity requestEntity = new HttpEntity<>(params, reqHeaders);
        return restTemplate.exchange(uri, HttpMethod.GET, requestEntity, JsonNode.class, params);
    }

    public static ResponseEntity<String> restPostToString(Map<String, Object> params, String uri, HttpHeaders reqHeaders,
                                                          String charset){
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(params, reqHeaders);
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> httpMessageConverter : list) {
            if (httpMessageConverter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) httpMessageConverter).setDefaultCharset(Charset.forName(charset));
                break;
            }
        }
        return restTemplate.postForEntity(uri, entity, String.class);
    }

}
