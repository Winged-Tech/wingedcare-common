package com.wingedtech.common.message;

import com.wingedtech.common.message.dto.GenericTemplateMessageDTO;

/**
 * @author Jason
 * @since 2019-04-27 12:30
 */
public class MessageHelper {

    private final TemplateMessageSource templateMessageSource;

    public MessageHelper(TemplateMessageSource templateMessageSource) {
        this.templateMessageSource = templateMessageSource;
    }

    public boolean sendTemplateMessage(GenericTemplateMessageDTO message) {
        return StreamHelper.sendBinaryPayloadToStream(templateMessageSource.publishTemplateMessageEvent(), message);
    }
}
