package com.wingedtech.common.service.beloging;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 实现ObjectWithUserId的DTO对象基类
 */
@Data
public class DTOWithUserId implements ObjectWithUserId, Serializable {

    /**
     * 该记录所属用户的id，默认情况下是userLogin
     */
    @ApiModelProperty("该记录所属用户的id，默认情况下是userLogin")
    private String userId;
}