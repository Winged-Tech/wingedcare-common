package com.wingedtech.common.message.bpm;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface BpmMessageEventSink {
    /**
     * Input channel name
     */
    String INPUT = "bmp-message-event-in";

    @Input(INPUT)
    SubscribableChannel receiveMessageEvent();
}
