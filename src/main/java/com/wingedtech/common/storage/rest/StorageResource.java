package com.wingedtech.common.storage.rest;

import com.wingedtech.common.storage.ObjectStorageItem;
import com.wingedtech.common.storage.ObjectStorageItemAccessOptions;
import com.wingedtech.common.storage.ObjectStorageService;
import com.wingedtech.common.storage.ObjectStorageType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * 资源访问resource，用于提供资源访问的API接口
 */
public class StorageResource {
    private final ObjectStorageService objectStorageService;

    /**
     * 当前服务名
     */
    @Value("${spring.application.name}")
    private String serviceName;

    public StorageResource(ObjectStorageService objectStorageService) {
        this.objectStorageService = objectStorageService;
    }

    /**
     * 获取指定的资源并重定向
     * @param resourcePath 资源文件相对路径
     * @param resourceName 资源配置名称
     * @param fileName 最终下载的实际文件名
     * @param download 是否下载文件
     * @return 302重定向到指定的资源
     */
    public ResponseEntity<String> redirectToResource(String resourcePath, String resourceName, String fileName, boolean download) throws URISyntaxException {
        return redirectToResource(resourcePath, resourceName, fileName, download, null);
    }

    /**
     * 获取指定的资源并重定向
     * @param resourcePath 资源文件相对路径
     * @param resourceName 资源配置名称
     * @param fileName 最终下载的实际文件名
     * @param download 是否下载文件
     * @param styleName 图片访问的样式名称，可为空
     * @return 302重定向到指定的资源
     */
    public ResponseEntity<String> redirectToResource(String resourcePath, String resourceName, String fileName, boolean download, String styleName) throws URISyntaxException {
        String finalUrl = getRedirectUrl(resourcePath, resourceName, fileName, download, styleName);

        if (!isDirectAccessibleUrl(finalUrl)) {
            return redirectToDownloadApi(resourcePath, objectStorageService.getResourceStorageType(resourceName), fileName, download);
        }

        return buildResponse(finalUrl);
    }

    /**
     * 获取指定的资源并重定向
     * @param resourcePath 资源文件相对路径
     * @param type 资源文件类型
     * @param fileName 最终下载的实际文件名
     * @param download 是否下载文件
     * @param styleName 图片访问的样式名称，可为空
     * @return 302重定向到指定的资源
     */
    public ResponseEntity<String> redirectToResource(String resourcePath, ObjectStorageType type, String fileName, boolean download, String styleName) throws URISyntaxException {
        String finalUrl = getRedirectUrl(resourcePath, type, fileName, download, styleName);
        if (!isDirectAccessibleUrl(finalUrl)) {
            return redirectToDownloadApi(resourcePath, type, fileName, download);
        }

        return buildResponse(finalUrl);
    }

    /**
     * 根据指定的资源路径, 重定向到本服务的直接下载的接口
     * @param resourcePath
     * @param type
     * @param fileName
     * @param download
     * @return
     * @throws URISyntaxException
     */
    public ResponseEntity<String> redirectToDownloadApi(String resourcePath, ObjectStorageType type, String fileName, boolean download) throws URISyntaxException {
        final String finalUrl = formatDirectDownloadUrl(resourcePath, type, fileName, download, null);
        return buildResponse(finalUrl);
    }

    private String formatDirectDownloadUrl(String resourcePath, ObjectStorageType type, String fileName, boolean download, HttpServletRequest request) {
        // 最新的jhipster的网关代码中, 多了一层 /services 的路径
        final UriComponentsBuilder builder = UriComponentsBuilder.fromPath(String.format("/services/%s%s", serviceName, DirectDownloadResource.API_DIRECT_UPLOAD));
        if (request != null) {
            builder.scheme(request.getScheme()).port(request.getServerPort()).host(request.getServerName());
        }
        builder.queryParam("type", type);
        builder.queryParam("storagePath", resourcePath);
        if (fileName != null) {
            builder.queryParam("fileName", fileName);
        }
        builder.queryParam("download", download);
        final String finalUrl = builder.toUriString();
        return finalUrl;
    }

    private static ResponseEntity<String> buildResponse(String finalUrl) throws URISyntaxException {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(new URI(finalUrl));
        return ResponseEntity.status(HttpStatus.FOUND).headers(responseHeaders).body(finalUrl);
    }

    /**
     * 获取指定的资源重定向地址
     * @param resourcePath
     * @param resourceName
     * @param fileName
     * @param download
     * @return
     */
    public String getRedirectUrl(String resourcePath, String resourceName, String fileName, boolean download, String styleName) {
        return getRedirectUrl(resourcePath, resourceName, fileName, download, styleName, false, null);
    }

    /**
     * 获取指定的资源重定向地址
     * @param resourcePath
     * @param resourceName
     * @param fileName
     * @param download
     * @param ensureHttp
     * @param currentRequestHost
     * @return
     */
    public String getRedirectUrl(String resourcePath, String resourceName, String fileName, boolean download, String styleName, boolean ensureHttp, HttpServletRequest currentRequestHost) {
        final ObjectStorageItem item = ObjectStorageService.newStorageItemToGet(resourceName, resourcePath);
        return getObjectAccessUrl(fileName, download, styleName, item, ensureHttp, currentRequestHost);
    }

    /**
     * 获取对象的访问url
     * @param fileName
     * @param download
     * @param styleName
     * @param item
     * @param ensureHttp 是否强制Http接口
     * @param currentRequest
     * @return
     */
    private String getObjectAccessUrl(String fileName, boolean download, String styleName, ObjectStorageItem item, boolean ensureHttp, HttpServletRequest currentRequest) {
        final String objectAccessUrl = objectStorageService.getObjectAccessUrl(item, ObjectStorageItemAccessOptions.builder().download(download).fileName(fileName).imageStyle(styleName).build());
        // 如果强制Http类型接口
        if (ensureHttp && !isDirectAccessibleUrl(objectAccessUrl)) {
            return formatDirectDownloadUrl(item.getStoragePath(), item.getType(), fileName, download, currentRequest);
        }
        return objectAccessUrl;
    }

    /**
     * 根据指定的资源存储类型，获取指定的资源重定向地址
     * @param resourcePath
     * @param type
     * @param fileName
     * @param download
     * @return
     */
    public String getRedirectUrl(String resourcePath, ObjectStorageType type, String fileName, boolean download, String styleName) {
        return getRedirectUrl(resourcePath, type, fileName, download, styleName, false, null);
    }

    /**
     * 根据指定的资源存储类型，获取指定的资源重定向地址
     * @param resourcePath
     * @param type
     * @param fileName
     * @param download
     * @param ensureHttp
     * @param currentRequestHost
     * @return
     */
    public String getRedirectUrl(String resourcePath, ObjectStorageType type, String fileName, boolean download, String styleName, boolean ensureHttp, HttpServletRequest currentRequestHost) {
        final ObjectStorageItem item = ObjectStorageService.newStorageItemToGet(type, resourcePath);
        return getObjectAccessUrl(fileName, download, styleName, item, ensureHttp, currentRequestHost);
    }

    /**
     * 根据资源，获取直传策略
     * @param objectStorageItem
     * @return
     */
    public ResponseEntity<Map<String, String>> getDirectUploadingPolicy(ObjectStorageItem objectStorageItem){
        return ResponseEntity.ok(objectStorageService.getDirectUploadingPolicy(objectStorageItem));
    }

    /**
     * 获取临时的访问凭证信息
     * @param resourceName
     * @return
     */
    public Map<String, String> getTemporaryAccessToken(String resourceName) {
        return objectStorageService.getTemporaryAccessToken(resourceName);
    }

    /**
     * 判定指定的url是否可直接被浏览器访问
     * @param url
     * @return
     */
    public static boolean isDirectAccessibleUrl(String url) {
        return url.startsWith("http");
    }
}
