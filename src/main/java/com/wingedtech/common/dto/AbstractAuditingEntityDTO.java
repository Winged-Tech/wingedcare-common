package com.wingedtech.common.dto;

import java.io.Serializable;
import java.time.Instant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Base abstract class for entities which will hold definitions for created, last modified by and created,
 * last modified by date.
 */
@Data
public abstract class AbstractAuditingEntityDTO implements Serializable, SensitiveData {

	private static final long serialVersionUID = 7362468593149245022L;
	
	@ApiModelProperty(value = "创建者，通常为用户的login", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String createdBy;

    @ApiModelProperty(value = "创建时间", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private Instant createdDate;

    @ApiModelProperty(value = "最后一次修改人，通常为用户的login", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String lastModifiedBy;

    @ApiModelProperty(value = "最后一次修改时间", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private Instant lastModifiedDate;

    /**
     * 从另一个实例拷贝数据
     * @param dto
     */
    public void copy(AbstractAuditingEntityDTO dto) {
        setCreatedBy(dto.getCreatedBy());
        setCreatedDate(dto.getCreatedDate());
        setLastModifiedBy(dto.getLastModifiedBy());
        setLastModifiedDate(dto.getLastModifiedDate());
    }

    /**
     * 数据脱敏
     */
    @Override
    public void desensitize() {
        setCreatedBy(null);
        setLastModifiedBy(null);
    }
}
