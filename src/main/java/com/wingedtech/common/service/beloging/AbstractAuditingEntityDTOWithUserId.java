package com.wingedtech.common.service.beloging;

import com.wingedtech.common.dto.AbstractAuditingEntityDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AbstractAuditingEntityDTOWithUserId extends AbstractAuditingEntityDTO implements ObjectWithUserId {
    /**
     * 该记录所属用户的id，默认情况下是userLogin
     */
    @ApiModelProperty("该记录所属用户的id，默认情况下是userLogin")
    private String userId;
}
