package com.wingedtech.common.storage.rest;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import com.wingedtech.common.storage.ObjectStorageItem;
import com.wingedtech.common.storage.ObjectStorageService;
import com.wingedtech.common.storage.ObjectStorageType;
import com.wingedtech.common.util.HeaderValuesUtils;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;

/**
 * 二进制文件下载接口
 */
@RestController
@Slf4j
public class DirectDownloadResource extends StorageResource {
    public static final String API_DIRECT_UPLOAD = "/api/oss/direct-download";
    private final ObjectStorageService objectStorageService;

    public DirectDownloadResource(ObjectStorageService objectStorageService) {
        super(objectStorageService);
        this.objectStorageService = objectStorageService;
    }

    @GetMapping(API_DIRECT_UPLOAD)
    public void directDownload(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam @NotNull @ApiParam("文件存储类型") ObjectStorageType type,
                               @RequestParam @NotNull @ApiParam("文件存储的相对路径") String storagePath,
                               @RequestParam(required = false) @ApiParam("文件的最终下载文件名") String fileName,
                               @RequestParam(defaultValue = "false") @ApiParam("是否强制文件下载") boolean download,
                               @RequestParam(required = false) @ApiParam("图片访问的样式名称") String styleName) throws IOException {
        final InputStream object = objectStorageService.getObjectWithoutPreprocess(new ObjectStorageItem(null, type, storagePath));

        if (Strings.isNullOrEmpty(fileName)) {
            fileName = FilenameUtils.getName(storagePath);
        }

        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, download ? HeaderValuesUtils.contentDispositionValues(request, fileName) : HeaderValuesUtils.contentDispositionInline(request, fileName));

        StreamUtils.copy(object, response.getOutputStream());
    }
}
