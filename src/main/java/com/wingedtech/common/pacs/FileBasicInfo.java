package com.wingedtech.common.pacs;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件基本信息
 *
 * @author zhangyp
 */
@Data
public class FileBasicInfo implements Serializable {
    private static final long serialVersionUID = 5579606196302214134L;

    @ApiModelProperty("文件上传的资源名")
    private String resourceConfig;

    @ApiModelProperty("云端地址")
    private String ossUrl;

    @ApiModelProperty("源文件名")
    private String name;

    public FileBasicInfo() {
    }

    public <T extends FileBasicInfo> FileBasicInfo(T fileInfo) {
        this.resourceConfig = fileInfo.getResourceConfig();
        this.ossUrl = fileInfo.getOssUrl();
        this.name = fileInfo.getName();
    }

}
