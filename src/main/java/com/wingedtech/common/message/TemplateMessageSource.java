package com.wingedtech.common.message;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * TODO description
 *
 * @author Jason
 * @since 2019-04-27 11:20
 */
public interface TemplateMessageSource {

    /**
     * Specify the binding target name
     */
    String TEMPLATE_OUTPUT = "template-messages-out";

    /**
     * 发布模板消息
     *
     * @return
     */
    @Output(TEMPLATE_OUTPUT)
    MessageChannel publishTemplateMessageEvent();
}
