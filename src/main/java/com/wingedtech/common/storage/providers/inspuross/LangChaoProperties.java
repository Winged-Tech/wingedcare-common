package com.wingedtech.common.storage.providers.inspuross;

import lombok.Data;

/**
 * @author ssy
 * @date 2020/11/25 15:43
 */
@Data
public class LangChaoProperties {
    private String endPointInternal;
    private String endPointExternal;
    private String endPointInternalDomain;
    private String endPointExternalDomain;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private Long signedUrlExpirationSeconds = 30L * 60L;
}
