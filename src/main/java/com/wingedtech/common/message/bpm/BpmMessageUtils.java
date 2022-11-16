package com.wingedtech.common.message.bpm;

import lombok.extern.slf4j.Slf4j;

/**
 * @author sunbjx
 * @since 2019-01-22 18:28
 */
@Slf4j
public class BpmMessageUtils {
    /**
     * 开启一个流程
     *
     * @param helper         bpm streams util
     * @param definitionsKey 流程定义key
     * @param businessId     关联的业务id
     * @param variablesName  变量名
     * @param variables      变量值
     */
    public static void startWorkflow(BpmMessageEventStreamHelper helper,
                                     String definitionsKey,
                                     String businessId,
                                     String variablesName,
                                     Object variables) {
        log.debug("Starting bpm workflow by businessKey : {}, variablesName : {}", definitionsKey.concat(businessId), variablesName);

        BpmMessageEvent messageEvent = BpmMessageEventStreamHelper
            .newMessageEvent(definitionsKey, definitionsKey.concat(businessId), true)
            .withVariable(variablesName, variables);

        if (!helper.sendMessageEvent(messageEvent)) {
            log.error("Failed to send streams message: {}", messageEvent);
        } else {
            log.info("Sent to streams message: {}", messageEvent);
        }
    }

    /**
     * 推送
     *
     * @param helper         bpm streams util
     * @param definitionsKey 流程定义 key
     * @param businessId     关联的业务id
     * @param messageName    消息名称
     */
    public static void sendIntermediate(BpmMessageEventStreamHelper helper,
                                        String definitionsKey,
                                        String businessId,
                                        String messageName) {
        String businessKey = definitionsKey.concat(businessId);
        log.debug("Send intermediate message for bpm workflow by businessKey : {}, messageName : {}", businessKey, messageName);
        if (helper.sendMessageEvent(messageName, businessKey, false)) {
            log.debug("Sent {} to bpm for {}, {} success.", messageName, definitionsKey, businessKey);
        } else {
            log.error("Send {} to bpm for {}, {} failed.", messageName, definitionsKey, businessKey);
        }
    }

    /**
     * 推送
     *
     * @param helper         bpm streams util
     * @param definitionsKey 流程定义 key
     * @param businessId     关联的业务id
     * @param messageName    消息名称
     */
    public static void sendIntermediateWithVariable(BpmMessageEventStreamHelper helper,
                                                    String definitionsKey,
                                                    String businessId,
                                                    String messageName,
                                                    String variablesName,
                                                    Object variables) {
        String businessKey = definitionsKey.concat(businessId);
        log.debug("Send intermediate message for bpm workflow by businessKey : {}, messageName : {}, variables : {}", businessKey, messageName, variables.toString());
        BpmMessageEvent messageEvent = BpmMessageEventStreamHelper
            .newMessageEvent(messageName, definitionsKey + businessId, false)
            .withVariable(variablesName, variables);
        if (helper.sendMessageEvent(messageEvent)) {
            log.debug("Sent {} to bpm for {}, {} success.", messageName, definitionsKey, businessKey);
        } else {
            log.error("Send {} to bpm for {}, {} failed.", messageName, definitionsKey, businessKey);
        }
    }
}
