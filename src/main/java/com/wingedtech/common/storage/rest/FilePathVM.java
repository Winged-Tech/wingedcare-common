package com.wingedtech.common.storage.rest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("接口用来以json格式返回文件路劲的view model")
@Data
public class FilePathVM {

    @ApiModelProperty("返回的file路劲")
    private String file;

    public FilePathVM(String file) {
        this.file = file;
    }
}
