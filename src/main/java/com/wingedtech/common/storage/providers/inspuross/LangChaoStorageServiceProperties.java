package com.wingedtech.common.storage.providers.inspuross;

import com.wingedtech.common.storage.ObjectStorageType;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ssy
 * @date 2020/11/25 15:57
 */
@ConfigurationProperties(prefix = "winged.oss.inspuross")
public class LangChaoStorageServiceProperties {
    private LangChaoProperties publicResource;
    private LangChaoProperties privateResource;

    public LangChaoProperties getProperties(ObjectStorageType type) {
        switch (type) {
            case PUBLIC_RESOURCE:
                return publicResource;
            case PRIVATE_RESOURCE:
                return privateResource;
            default:
                return null;
        }
    }

    public LangChaoProperties getPublicResource() {
        return publicResource;
    }

    public void setPublicResource(LangChaoProperties publicResource) {
        this.publicResource = publicResource;
    }

    public LangChaoProperties getPrivateResource() {
        return privateResource;
    }

    public void setPrivateResource(LangChaoProperties privateResource) {
        this.privateResource = privateResource;
    }
}
