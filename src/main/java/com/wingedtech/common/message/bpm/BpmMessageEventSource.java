package com.wingedtech.common.message.bpm;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface BpmMessageEventSource {
    /**
     * Output channel name
     */
    String OUTPUT = "bmp-message-event-out";

    @Output(OUTPUT)
    MessageChannel publishMessageEvent();
}
