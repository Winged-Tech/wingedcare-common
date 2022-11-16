package com.wingedtech.common.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class AddressDetails implements Serializable {

    private static final long serialVersionUID = -1117694441926424169L;

    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    private String provinceName;

    /**
     * 省编码
     */
    @ApiModelProperty(value = "省编码")
    private String provinceGbCode;

    /**
     * 市
     */
    @ApiModelProperty(value = "市")
    private String cityName;

    /**
     * 市编码
     */
    @ApiModelProperty(value = "市编码")
    private String cityGbCode;

    /**
     * 区
     */
    @ApiModelProperty(value = "区")
    private String districtName;

    /**
     * 区编码
     */
    @ApiModelProperty(value = "区编码")
    private String districtGbCode;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    @NotNull
    private String postAddress;
}
