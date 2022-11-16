package com.wingedtech.common.storage.rest;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.wingedtech.common.constant.Requests;
import com.wingedtech.common.storage.ObjectStorageItem;
import com.wingedtech.common.storage.ObjectStorageService;
import com.wingedtech.common.storage.ObjectStorageType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用的存储资源访问接口
 */
@RestController
@Api(value = "通用的存储资源访问接口", description = "通用的存储资源访问接口")
@Slf4j
public class CommonStorageResource extends StorageResource {

    public static final String PUBLIC_RESOURCE = Requests.API_PUBLIC + "/public-resource";
    public static final String PRIVATE_RESOURCE = "/api/private-resource";

    private final ObjectStorageService objectStorageService;

    public CommonStorageResource(ObjectStorageService objectStorageService) {
        super(objectStorageService);
        this.objectStorageService = objectStorageService;
    }

    /**
     * 重定向到指定公开资源的访问地址，此接口将总是返回302
     *
     * @param resourcePath
     * @param fileName
     * @param download
     * @param styleName
     * @return
     * @throws URISyntaxException
     */
    @ApiOperation("重定向到指定公开资源的访问地址，可直接作为图片链接用于图片显示（download设置为false），或者在新窗口中打开作为文件下载（download设置为true）")
    @GetMapping({PUBLIC_RESOURCE + "/get"})
    public ResponseEntity<String> redirectToPublicResource(
            @RequestParam @NotNull @ApiParam("文件存储的相对路径") String resourcePath,
            @RequestParam(required = false) @ApiParam("文件的最终下载文件名") String fileName,
            @RequestParam(defaultValue = "false") @ApiParam("是否强制文件下载") boolean download,
            @RequestParam(required = false) @ApiParam("图片访问的样式名称") String styleName) throws URISyntaxException {
        return super.redirectToResource(resourcePath, ObjectStorageType.PUBLIC_RESOURCE, fileName, download, styleName);
    }

    /**
     * 获取指定公开资源文件的最终链接（不带重定向）
     *
     * @param resourcePath
     * @param fileName
     * @param download
     * @param styleName
     * @return
     */
    @ApiOperation("获取指定公开资源文件的最终链接（不带重定向），可直接作为图片链接用于图片显示（download设置为false），或者在新窗口中打开作为文件下载（download设置为true）")
    @GetMapping({PUBLIC_RESOURCE + "/get-url"})
    public ResponseEntity<String> getPublicResourceFinalDownloadUrl(
        HttpServletRequest request,
        @RequestParam @NotNull @ApiParam("文件存储的相对路径") String resourcePath,
        @RequestParam(required = false) @ApiParam("文件的最终下载文件名") String fileName,
        @RequestParam(defaultValue = "false") @ApiParam("是否强制文件下载") boolean download,
        @RequestParam(required = false) @ApiParam("图片访问的样式名称") String styleName,
        @RequestParam(defaultValue = "false") @ApiParam("是否强制HTTP接口地址") boolean ensureHttp) {
        return ResponseEntity.ok(super.getRedirectUrl(resourcePath, ObjectStorageType.PUBLIC_RESOURCE, fileName, download, styleName, ensureHttp, request));
    }

    /**
     * 获取指定公开资源文件的最终链接json格式（不带重定向）
     *
     * @param resourcePath
     * @param fileName
     * @param download
     * @param styleName
     * @return
     */
    @ApiOperation("获取指定公开资源文件的JSON格式最终链接（不带重定向），可直接作为图片链接用于图片显示（download设置为false），或者在新窗口中打开作为文件下载（download设置为true）")
    @GetMapping({PUBLIC_RESOURCE + "/get-url-json"})
    public ResponseEntity<FilePathVM> getPublicResourceFinalDownloadUrlJson(
        HttpServletRequest request,
        @RequestParam @NotNull @ApiParam("文件存储的相对路径") String resourcePath,
        @RequestParam(required = false) @ApiParam("文件的最终下载文件名") String fileName,
        @RequestParam(defaultValue = "false") @ApiParam("是否强制文件下载") boolean download,
        @RequestParam(required = false) @ApiParam("图片访问的样式名称") String styleName,
        @RequestParam(defaultValue = "false") @ApiParam("是否强制HTTP接口地址") boolean ensureHttp) {
        return ResponseEntity.ok(new FilePathVM(super.getRedirectUrl(resourcePath, ObjectStorageType.PUBLIC_RESOURCE, fileName, download, styleName, ensureHttp, request)));
    }


    /**
     * 重定向到指定私有资源的访问地址，此接口将总是返回302
     *
     * @param resourcePath
     * @param fileName
     * @param download
     * @param styleName
     * @return
     * @throws URISyntaxException
     */
    @ApiOperation("重定向到指定私有访问资源的访问地址，可直接作为图片链接用于图片显示（download设置为false），或者在新窗口中打开作为文件下载（download设置为true）")
    @GetMapping({PRIVATE_RESOURCE + "/get"})
    public ResponseEntity<String> redirectToPrivateResource(
            @RequestParam @NotNull @ApiParam("文件存储的相对路径") String resourcePath,
            @RequestParam(required = false) @ApiParam("文件的最终下载文件名") String fileName,
            @RequestParam(defaultValue = "false") @ApiParam("是否强制文件下载") boolean download,
            @RequestParam(required = false) @ApiParam("图片访问的样式名称") String styleName) throws URISyntaxException {
        return super.redirectToResource(resourcePath, ObjectStorageType.PRIVATE_RESOURCE, fileName, download, styleName);
    }

    /**
     * 获取指定私有资源文件的最终链接（不带重定向）
     *
     * @param resourcePath
     * @param fileName
     * @param download
     * @param styleName
     * @return
     */
    @ApiOperation("获取指定私有访问资源文件的最终链接（不带重定向），可直接作为图片链接用于图片显示（download设置为false），或者在新窗口中打开作为文件下载（download设置为true）")
    @GetMapping({PRIVATE_RESOURCE + "/get-url"})
    public ResponseEntity<String> getPrivateResourceFinalDownloadUrl(
        HttpServletRequest request,
        @RequestParam @NotNull @ApiParam("文件存储的相对路径") String resourcePath,
        @RequestParam(required = false) @ApiParam("文件的最终下载文件名") String fileName,
        @RequestParam(defaultValue = "false") @ApiParam("是否强制文件下载") boolean download,
        @RequestParam(required = false) @ApiParam("图片访问的样式名称") String styleName,
        @RequestParam(defaultValue = "false") @ApiParam("是否强制HTTP接口地址") boolean ensureHttp) {
        return ResponseEntity.ok(super.getRedirectUrl(resourcePath, ObjectStorageType.PRIVATE_RESOURCE, fileName, download, styleName, ensureHttp, request));
    }

    /**
     * 获取指定私有资源文件的最终链接JSON格式（不带重定向）
     *
     * @param resourcePath
     * @param fileName
     * @param download
     * @param styleName
     * @return
     */
    @ApiOperation("获取指定私有访问资源文件的JSON格式最终链接（不带重定向），可直接作为图片链接用于图片显示（download设置为false），或者在新窗口中打开作为文件下载（download设置为true）")
    @GetMapping({PRIVATE_RESOURCE + "/get-url-json"})
    public ResponseEntity<FilePathVM> getPrivateResourceFinalDownloadUrlJson(
        HttpServletRequest request,
        @RequestParam @NotNull @ApiParam("文件存储的相对路径") String resourcePath,
        @RequestParam(required = false) @ApiParam("文件的最终下载文件名") String fileName,
        @RequestParam(defaultValue = "false") @ApiParam("是否强制文件下载") boolean download,
        @RequestParam(required = false) @ApiParam("图片访问的样式名称") String styleName,
        @RequestParam(defaultValue = "false") @ApiParam("是否强制HTTP接口地址") boolean ensureHttp) {
        return ResponseEntity.ok(new FilePathVM(super.getRedirectUrl(resourcePath, ObjectStorageType.PRIVATE_RESOURCE, fileName, download, styleName, ensureHttp, request)));
    }

    @PostMapping("/api/upload")
    @ApiOperation("通用上传接口，返回上传后的文件存储相对路径")
    public ResponseEntity<String> uploadResource(@NotNull @RequestPart MultipartFile file, @NotNull @RequestPart String resourceName, @RequestPart(required = false) String objectId) throws IOException {
        final InputStream inputStream = file.getInputStream();
        final String originalFilename = file.getOriginalFilename();
        return ResponseEntity.ok(uploadUserResource(inputStream, originalFilename, resourceName, objectId));
    }


    @PostMapping("/api/upload-json")
    @ApiOperation("通用上传接口，为当前用户上传一个附件文件，可以是属于该用户的头像、证书图片等，返回上传后的文件存储相对路径（JSON格式）。可通过key参数指定返回数据里的字段名称，缺省值为file")
    public ResponseEntity<Map<String, String>> uploadResourceJson(@NotNull @RequestPart MultipartFile file, @NotNull @RequestPart String resourceName, @RequestPart(required = false) String objectId, @RequestPart(required = false) String key) throws IOException {
        final InputStream inputStream = file.getInputStream();
        final String originalFilename = file.getOriginalFilename();
        final HashMap<String, String> result = Maps.newHashMap();
        final String filePath = uploadUserResource(inputStream, originalFilename, resourceName, objectId);
        result.put(MoreObjects.firstNonNull(key, "file"), filePath);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/upload-policy")
    @ApiOperation("数据文件上传策略获取")
    public ResponseEntity<Map<String, String>> getAllSampleEntities(@RequestBody ObjectStorageItem objectStorageItem) {
        log.debug("REST request to get upload-policy");
        return ResponseEntity.ok(objectStorageService.getDirectUploadingPolicy(objectStorageItem));
    }

    /**
     * 获取临时的访问凭证信息
     * @param resourceName
     * @return
     */
    @ApiOperation("获取临时的访问凭证信息")
    @GetMapping("/api/sts")
    public ResponseEntity<Map<String, String>> getSTSToken(@RequestParam String resourceName) {
        return ResponseEntity.ok(super.getTemporaryAccessToken(resourceName));
    }

    /**
     * 用户上传
     *
     * @param inputStream
     * @param originalFilename
     * @param resourceName
     * @param objectId
     * @return
     */
    private String uploadUserResource(InputStream inputStream, String originalFilename, String resourceName, String objectId) {
        ObjectStorageItem storageItem;
        if (objectId != null) {
            storageItem = ObjectStorageService.newStorageItemToPutWithObjectId(resourceName, objectId, originalFilename);
        } else {
            storageItem = ObjectStorageService.newStorageItemToPut(resourceName, originalFilename);
        }
        return objectStorageService.putObject(inputStream, storageItem);
    }
}
