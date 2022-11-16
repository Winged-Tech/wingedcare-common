package com.wingedtech.common.message.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 通用模板消息
 *
 * @author Jason
 * @since 2019-05-23 16:35
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class GenericTemplateMessageDTO extends TemplateMessageDTO {

    private static final long serialVersionUID = -363613321254074003L;

    /**
     * 组织id 用于确认同一消息业务下不同组织的 businessKey
     */
    private String organizationId;

    /**
     * 内部服务 用于确认用户微信信息 value : userLogin
     */
    private String recipientLogin;

}
