package com.wingedtech.common.message.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Created on 2018/7/3.
 *
 * @author ssy
 */
@Data
public class OpenIds implements Serializable {

    private static final long serialVersionUID = 4538741491841081205L;

    /**
     * 公众号openid
     */
    private String mp;
    /**
     * PC平台openid
     */
    private String pc;
    /**
     * 小程序openid
     */
    private String ma;

    public static boolean isNotBlank(OpenIds openIds) {
        return openIds != null && openIds.isNotBlank();
    }

    public static boolean isBlank(OpenIds openIds) {
        return openIds == null || openIds.isBlank();
    }

    @JsonIgnore
    private boolean isNotBlank() {
        return StringUtils.isNotBlank(mp) || StringUtils.isNotBlank(pc) || StringUtils.isNotBlank(ma);
    }

    @JsonIgnore
    private boolean isBlank() {
        return !isNotBlank();
    }
}
