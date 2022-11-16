package com.wingedtech.common.errors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.zalando.problem.Problem;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

@Slf4j
public class ResponseUtils {

    public static ResponseEntity<Problem> wrapResponse(ResponseEntity<Problem> entity) {
        if (entity.getBody() instanceof BusinessException) {
            return new ResponseEntity<>(entity.getBody(), wrapHeaders(entity), entity.getStatusCode());
        }
        return entity;
    }

    public static MultiValueMap<String, String> wrapHeaders(ResponseEntity<Problem> entity) {
        if (entity.getBody() instanceof BusinessException) {
            BusinessException be = (BusinessException) entity.getBody();
            final HttpHeaders headers = entity.getHeaders();
            MultiValueMap<String, String> map;
            if (headers != null) {
                map = new LinkedMultiValueMap(headers);
            }
            else {
                map = new LinkedMultiValueMap<>(1);
            }

            map.add(BusinessException.HTTP_HEADER, be.getCode());
            return map;
        }
        return entity.getHeaders();
    }

    public static void extractAndThrowBusinessException(Response response) {
        final BusinessException be = extractBusinessException(response);
        if (be != null) {
            throw be;
        }
    }

    public static BusinessException extractBusinessException(Response response) {
        final Map<String, Collection<String>> headers = response.headers();
        if (headers != null) {
            final Collection<String> header = headers.get(BusinessException.HTTP_HEADER);
            if (header != null) {
                try {
                    final InputStream bodyStream = response.body().asInputStream();
                    return parseBusinessExceptionDetails(header, bodyStream);
                } catch (Exception e) {
                    log.warn("Found BusinessException header in response, but failed to extract", e);
                }
            }
        }
        return null;
    }

    public static BusinessException parseBusinessExceptionDetails(Collection<String> header, InputStream bodyStream) throws IOException {
        String code = Iterables.getFirst(header, null);
        String detail = null;
        final JsonNode jsonNode = new ObjectMapper().readTree(bodyStream);
        final JsonNode node = jsonNode.get("detail");
        if (node != null) {
            detail = node.asText();
        }
        log.debug("Extracted BusinessException from response with code {} and detail {}", code, detail);
        return new BusinessException(code, detail);
    }
}
