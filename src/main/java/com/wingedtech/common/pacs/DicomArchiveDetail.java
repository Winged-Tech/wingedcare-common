package com.wingedtech.common.pacs;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * Dicom文件存档详情
 *
 * @author zhangyp
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DicomArchiveDetail extends FileBasicInfo implements Serializable {
    private static final long serialVersionUID = 940201003603150794L;

    @ApiModelProperty("Dicom: StudyInstanceUid")
    private String studyInstanceUid;

    @ApiModelProperty("Dicom: Study存档地址")
    private String studyUrl;

    public DicomArchiveDetail() {
        super();
    }

    public <T extends FileBasicInfo> DicomArchiveDetail(T fileInfo) {
        super(fileInfo);
    }
}
