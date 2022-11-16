package com.wingedtech.common.domain;

import javax.validation.constraints.NotBlank;

/**
 * 具有唯一标识符的对象
 * @author taozhou
 */
public interface ObjectWithId {
    /**
     * 获取对象的唯一标识符
     * @return
     */
    @NotBlank String getId();
}
