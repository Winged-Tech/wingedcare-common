package com.wingedtech.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

/**
 * 顶级实体类
 *
 * @author Jason
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private String createdBy;

    @JsonIgnore
    private Instant createdDate;

    @JsonIgnore
    private String lastModifiedBy;

    @JsonIgnore
    private Instant lastModifiedDate;

}
