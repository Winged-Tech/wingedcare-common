package com.wingedtech.common.message.dto;

import com.wingedtech.common.time.DateTimeUtils;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

/**
 * 延时发送设置
 *
 * @author zhangyp
 */
@Data
public class DelaySendDTO implements Serializable {

    /**
     * 是否延时发送
     */
    private Boolean delayMode;

    /**
     * 延时发送时间(暂仅支持整点与半点时间)
     */
    private Instant delayTime;

    /**
     * 延时发送是否已处理
     */
    private Boolean delaySendHandle;


    public static DelaySendDTO setUpDelay(Instant delayTime) {
        DelaySendDTO delaySendDTO = new DelaySendDTO();
        // 延时发送模式
        delaySendDTO.setDelayMode(Boolean.TRUE);
        // 延时时间: 指定时间的上一个整点或半点
        delaySendDTO.setDelayTime(DateTimeUtils.getThePreviousWholeOrHalfHour(delayTime));
        // 未处理
        delaySendDTO.setDelaySendHandle(Boolean.FALSE);
        return delaySendDTO;
    }
}
