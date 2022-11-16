package com.wingedtech.common.storage.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

import static com.wingedtech.common.storage.rest.DirectUploadResource.API_DIRECT_UPLOAD;

@Data
@ConfigurationProperties(prefix = ObjectStorageResourceConfigProperties.WINGED_OSS_CONFIG)
public class ObjectStorageResourceConfigProperties {

    /**
     * resource config name for default config.
     */
    public static final String RESOURCE_CONFIG_DEFAULT = "default";
    public static final String WINGED_OSS_ROOT = "winged.oss";
    public static final String WINGED_OSS_CONFIG = WINGED_OSS_ROOT + ".config";
    public static final String WINGED_OSS_PROVIDER = WINGED_OSS_CONFIG + ".provider";
    public static final String ENABLE_DIRECT_UPLOAD = "enableDirectUpload";
    public static final String ENABLE_DIRECT_DOWNLOAD = "enableDirectDownload";

    private String provider;

    private boolean enableDirectUpload = false;

    private boolean enableDirectDownload = false;

    private List<ObjectStorageResourceProperties> resources = new ArrayList<>();

    public ObjectStorageResourceProperties getResourceConfig(String resourceName) {
        if (resources != null) {
            for (int i = 0; i < resources.size(); i ++) {
                ObjectStorageResourceProperties config = resources.get(i);
                if (StringUtils.equals(config.getResourceName(), resourceName)) {
                    return config;
                }
            }
        }
        return null;
    }

    public void addResourceConfig(ObjectStorageResourceProperties config) {
        this.resources.add(config);
    }

    public ObjectStorageResourceProperties getDefault() {
        ObjectStorageResourceProperties defaultConfig = getResourceConfig(RESOURCE_CONFIG_DEFAULT);

        if (defaultConfig == null) {
            defaultConfig = new ObjectStorageResourceProperties();
            this.addResourceConfig(defaultConfig);
        }

        return defaultConfig;
    }

    public List<ObjectStorageResourceProperties> getResources() {
        return resources;
    }

    public void setResources(List<ObjectStorageResourceProperties> resources) {
        this.resources = resources;
    }
}
