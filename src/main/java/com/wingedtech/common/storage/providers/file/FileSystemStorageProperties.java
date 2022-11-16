package com.wingedtech.common.storage.providers.file;

import com.google.common.base.Strings;
import com.wingedtech.common.storage.config.ObjectStorageResourceConfigProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static com.wingedtech.common.storage.rest.DirectUploadResource.API_DIRECT_UPLOAD;

@Data
@Slf4j
@ConfigurationProperties(prefix = ObjectStorageResourceConfigProperties.WINGED_OSS_ROOT + ".file")
public class FileSystemStorageProperties {
    /**
     * 本地文件系统存储的根目录
     */
    private String root;
    /**
     * 如果打开, 在写入文件的时候仅进行一次日志打印, 跳过真实的文件存储
     */
    private boolean mockWrite = false;

    /**
     * 是否使用默认的本地文件路径座位access url
     */
    private boolean useFilePathUrl = true;

    private String directUploadApi = "/messages" + API_DIRECT_UPLOAD;

    private String privateAccessUrl;

    private String publicAccessUrl;

    /**
     * 是否自动设置文件权限
     */
    private boolean autoSetFilePermission = false;

    /**
     * 可变更数数据文件配置的存放路径
     */
    private String changeRootConfigFilePath;

    /**
     * 动态修改winged.oss.file.root
     */
    public String getRoot() {
        if (Strings.isNullOrEmpty(changeRootConfigFilePath)) {
            return root;
        }
        File file = new File(changeRootConfigFilePath);
        if (file.isFile()) {
            try {
                String newRoot = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                if (Strings.isNullOrEmpty(newRoot)) {
                    return root;
                }
                return newRoot;
            } catch (Exception e) {
                log.error("Error file convert: {}", e.getMessage());
                return root;
            }
        }
        return root;
    }
}
