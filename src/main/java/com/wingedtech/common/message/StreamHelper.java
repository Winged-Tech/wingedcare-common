package com.wingedtech.common.message;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.io.Serializable;

/**
 * stream相关操作的helper工具类
 *
 * @author sunbjx
 * @since 2019-01-22 18:34
 */
public class StreamHelper {

    /**
     * 将指定的payload对象进行二进制序列化并发送到指定的MessageChannel
     *
     * @return 消息是否发送成功
     */
    public static <T extends Serializable> boolean sendBinaryPayloadToStream(MessageChannel channel, T payload) {
        return channel.send(MessageBuilder.withPayload(SerializationUtils.serialize(payload)).build());
    }
}
