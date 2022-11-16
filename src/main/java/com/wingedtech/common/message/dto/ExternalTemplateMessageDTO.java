package com.wingedtech.common.message.dto;

import com.wingedtech.common.message.dto.OpenIds;
import com.wingedtech.common.message.dto.TemplateMessageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

/**
 * 通用模板消息
 *
 * @author Jason
 * @since 2019-05-23 16:35
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class ExternalTemplateMessageDTO extends TemplateMessageDTO {

    private static final long serialVersionUID = -1447166096502404246L;

    /**
     * 用户的微信openid
     */
    private OpenIds openIds;


    /**
     * 用户的微信unionid
     */
    private String unionId;

    /**
     * 对应模版参数颜色
     */
    private Map<String, String> parametersColor;
}
