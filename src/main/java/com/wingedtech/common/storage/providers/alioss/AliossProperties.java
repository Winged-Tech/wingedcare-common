package com.wingedtech.common.storage.providers.alioss;

import lombok.Data;

@Data
public class AliossProperties {
    private String endPointInternal;
    private String endPointExternal;
    private String endPointInternalDomain;
    private String endPointExternalDomain;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private int signedUrlExpirationSeconds = 30 * 60;
    private String roleArn;
    private String endPointSTS;
    private String region;
}
