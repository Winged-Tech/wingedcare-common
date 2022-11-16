package com.wingedtech.common.streams.check;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * 定义一个专用来在各个服务之间测试各个集成了streams微服务之间联通性的Stream
 */
public interface CheckingStream {

    /**
     * Specify the binding target name
     */
    String INPUT = "check-in";

    /**
     * Specify the binding target name
     */
    String OUTPUT = "check-out";


    @Input(INPUT)
    SubscribableChannel receiveMessageEvent();

    @Output(OUTPUT)
    MessageChannel publishMessageEvent();
}
