package com.wingedtech.common.message.bpm;

import com.wingedtech.common.message.StreamHelper;

import java.util.Map;

/**
 * @author sunbjx
 * @since 2019-01-22 18:45
 */
public class BpmMessageEventStreamHelper {

    private final BpmMessageEventSource stream;

    public BpmMessageEventStreamHelper(BpmMessageEventSource stream) {
        this.stream = stream;
    }

    public static BpmMessageEvent newMessageEvent(String messageName, String businessKey, boolean isStartEvent) {
        BpmMessageEvent messageEvent = new BpmMessageEvent();
        messageEvent.setMessageName(messageName);
        messageEvent.setBusinessKey(businessKey);
        messageEvent.setStartEvent(isStartEvent);
        return messageEvent;
    }

    public boolean sendMessageEvent(String messageName, String businessKey, boolean isStartEvent) {
        return this.sendMessageEvent(messageName, businessKey, null, isStartEvent);
    }

    public boolean sendMessageEvent(String messageName, String businessKey, Map<String, Object> variables, boolean isStartEvent) {
        BpmMessageEvent messageEvent = newMessageEvent(messageName, businessKey, isStartEvent);
        messageEvent.setVariables(variables);
        return this.sendMessageEvent(messageEvent);
    }

    public boolean sendMessageEvent(BpmMessageEvent messageEvent) {
        return StreamHelper.sendBinaryPayloadToStream(this.stream.publishMessageEvent(), messageEvent);
    }
}
