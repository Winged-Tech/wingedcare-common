package com.wingedtech.common.pacs;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * DICOM存档信息
 *
 * @author zhangyp
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DicomArchiveInformation extends FileBasicInfo implements Serializable {
    private static final long serialVersionUID = -4167122801894840601L;

    @ApiModelProperty("档案号")
    private String medicalCaseId;

    @ApiModelProperty("患者Id")
    private String patientId;

    @ApiModelProperty("是否为DICOM文件")
    private Boolean isDicom;

    @ApiModelProperty("Dicom: 存档后详情")
    private List<DicomArchiveDetail> details;

    @ApiModelProperty("Dicom: 扩展tag信息")
    private Map<String, Object> extendInfo;

    public DicomArchiveInformation() {
    }

    public <T extends FileBasicInfo> DicomArchiveInformation(T fileInfo) {
        super(fileInfo);
    }
}
