package com.wingedtech.common.errors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.wingedtech.common.errors.ResponseUtils.parseBusinessExceptionDetails;

@Slf4j
public class BusinessExceptionResponseErrorHandler extends DefaultResponseErrorHandler {

    private ResponseErrorHandler handler;

    public BusinessExceptionResponseErrorHandler(ResponseErrorHandler handler) {
        this.handler = handler;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return super.hasError(response) || (handler != null && handler.hasError(response));
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        handlerBusinessException(response);
        handler.handleError(response);
    }

    @Override
    protected void handleError(ClientHttpResponse response, HttpStatus statusCode) throws IOException {
        handlerBusinessException(response);
        super.handleError(response, statusCode);
    }

    private void handlerBusinessException(ClientHttpResponse response) {
        final HttpHeaders headers = response.getHeaders();
        final List<String> header = headers.get(BusinessException.HTTP_HEADER);
        if (header != null) {
            BusinessException be = null;
            try {
                final InputStream bodyStream = response.getBody();
                be = parseBusinessExceptionDetails(header, bodyStream);
            } catch (Exception e) {
                log.error("Failed to parse BusinessException", e);
            }
            if (be != null) {
                throw be;
            }
        }
    }
}
