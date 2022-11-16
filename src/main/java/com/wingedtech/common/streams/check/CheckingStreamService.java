package com.wingedtech.common.streams.check;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.time.Instant;

@Service
@Slf4j
public class CheckingStreamService {

    private final CheckingStreamProperties checkingStreamProperties;
    private final CheckingStream checkingStream;

    /**
     * 当前服务名
     */
    @Value("${spring.application.name}")
    private String serviceName;

    public CheckingStreamService(CheckingStreamProperties checkingStreamProperties, CheckingStream checkingStream) {
        this.checkingStreamProperties = checkingStreamProperties;
        this.checkingStream = checkingStream;
    }

    @PostConstruct
    public void reportOnStartup() {
        if (checkingStreamProperties.isAutoReportOnStartup()) {
            this.report("PostConstruct greetings on startup");
        }
    }

    public CheckingMessage report() {
        return this.report(null);
    }

    /**
     * 让当前服务向其他服务广播一条消息, 用于测试streams通路
     */
    public CheckingMessage report(String message) {
        CheckingMessage checkingMessage = new CheckingMessage();
        checkingMessage.setSourceService(serviceName);
        checkingMessage.setCreatedTime(Instant.now());
        checkingMessage.setHost(getCurrentHostAddress());
        checkingMessage.setMessage(StringUtils.isNotBlank(message) ? message : "greetings");
        if (this.checkingStream.publishMessageEvent().send(MessageBuilder.withPayload(checkingMessage).build())) {
            log.info("[Checking] Successfully reported checking message: {}", checkingMessage);
        }
        else {
            log.info("[Checking] Failed to report checking message: {}", checkingMessage);
        }
        return checkingMessage;
    }

    public MessageChannel getReceivingChannel() {
        return this.checkingStream.receiveMessageEvent();
    }

    public MessageChannel getOutputChannel() {
        return this.checkingStream.publishMessageEvent();
    }

    @StreamListener(CheckingStream.INPUT)
    public void onCheckingMessage(CheckingMessage message) {
        log.info("[Checking] Message received: {}", message);
    }

    public static String getCurrentHostAddress() {
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        return hostAddress;
    }
}
