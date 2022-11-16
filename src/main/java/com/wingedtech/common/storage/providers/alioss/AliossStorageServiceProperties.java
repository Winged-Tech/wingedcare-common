package com.wingedtech.common.storage.providers.alioss;

import com.wingedtech.common.storage.ObjectStorageType;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "winged.oss.alioss")
public class AliossStorageServiceProperties {
    private AliossProperties publicResource;
    private AliossProperties privateResource;

    public AliossProperties getProperties(ObjectStorageType type) {
        switch (type) {
            case PUBLIC_RESOURCE:
                return publicResource;
            case PRIVATE_RESOURCE:
                return privateResource;
            default:
                return null;
        }
    }

    public AliossProperties getPublicResource() {
        return publicResource;
    }

    public void setPublicResource(AliossProperties publicResource) {
        this.publicResource = publicResource;
    }

    public AliossProperties getPrivateResource() {
        return privateResource;
    }

    public void setPrivateResource(AliossProperties privateResource) {
        this.privateResource = privateResource;
    }

}
