package com.wingedtech.common.storage.rest;

import com.wingedtech.common.storage.ObjectStorageItem;
import com.wingedtech.common.storage.ObjectStorageService;
import com.wingedtech.common.storage.config.ObjectStorageResourceConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * 直传接口
 */
@RestController
@Slf4j
public class DirectUploadResource extends StorageResource {
    public static final String API_DIRECT_UPLOAD = "/api/oss/direct-upload";
    private final ObjectStorageService objectStorageService;

    public DirectUploadResource(ObjectStorageService objectStorageService) {
        super(objectStorageService);
        this.objectStorageService = objectStorageService;
    }

    /**
     * 使用指定的资源路径直接存储文件
     * @param key
     * @param resource
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping({API_DIRECT_UPLOAD, "/api/direct-upload"})
    public ResponseEntity<Void> directUpload(@NotBlank @RequestPart String key, @NotBlank @RequestPart String resource, @NotNull @RequestPart MultipartFile file) throws IOException {
        final ObjectStorageItem objectStorageItem = ObjectStorageItem.get(resource, key);
        objectStorageService.putObjectWithoutPreprocess(file.getInputStream(), objectStorageItem);
        return ResponseEntity.noContent().build();
    }
}
