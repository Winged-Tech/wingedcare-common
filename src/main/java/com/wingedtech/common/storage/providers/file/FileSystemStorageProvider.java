package com.wingedtech.common.storage.providers.file;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.wingedtech.common.errors.BusinessException;
import com.wingedtech.common.storage.*;
import com.wingedtech.common.storage.config.ObjectStorageResourceProperties;
import com.wingedtech.common.storage.config.UploadingPolicyKeys;
import com.wingedtech.common.storage.errors.ObjectStorageException;
import com.wingedtech.common.storage.errors.ObjectStorageItemNotFoundException;
import com.wingedtech.common.util.OperatingSystem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.util.FileUtils;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.time.Instant;

@Slf4j
public class FileSystemStorageProvider implements ObjectStorageProvider {

    public static final ArrayList<PosixFilePermission> DIRECTORY_PERMISSIONS = Lists.newArrayList(PosixFilePermission.OTHERS_EXECUTE, PosixFilePermission.OTHERS_READ);
    public static final ArrayList<PosixFilePermission> FILE_PERMISSIONS = Lists.newArrayList(PosixFilePermission.OTHERS_READ);
    private final FileSystemStorageProperties fileSystemStorageProperties;

    private final String root;

    private final Path rootPath;

    public FileSystemStorageProvider(FileSystemStorageProperties fileSystemStorageProperties) {
        this.fileSystemStorageProperties = fileSystemStorageProperties;

        String root = fileSystemStorageProperties.getRoot();

        if (StringUtils.isBlank(root)) {
            root = getDefaultDirectory();
            log.info("No root directory is configured, using current user dir: {}", root);
        } else {
            if (!new File(root).isAbsolute()) {
                log.info("Configured root directory {} is not absolute, trying to make it absolute path...", root);
                root = buildAbsolutePath(root);
            }
        }
        this.root = root;
        this.rootPath = Paths.get(root);

        validate();
    }

    private String getDefaultDirectory() {
        return buildAbsolutePath("oss");
    }

    private static String buildAbsolutePath(String oss) {
        return FilenameUtils.concat(System.getProperty("user.dir"), oss);
    }

    private void validate() {
        final File file = new File(root);
        if (file.exists()) {
            log.debug("Root passed existence test");
        } else {
            log.info("Automatically create root {}", root);
            if (!file.mkdirs()) {
                log.error("Unable to create root {}", root);
                throw new IllegalStateException();
            }
        }
        if (file.isDirectory()) {
            log.debug("Root passed directory test");
        } else {
            log.error("Root {} is not a directory!", root);
            throw new IllegalStateException();
        }
        log.info("Root directory is: {}", root);
    }

    private File getFile(String storagePath) {
        if (ResourceUtils.isUrl(storagePath)) {
            try {
                return new File(new URL(storagePath).toURI());
            } catch (Exception e) {
                log.error("file path format error", e);
                throw new BusinessException("file path format error");
            }
        }
        if (Paths.get(storagePath).isAbsolute()) {
            return new File(storagePath);
        }
        return new File(FilenameUtils.concat(root, storagePath));
    }

    @Override
    public String pubObject(InputStream stream, ObjectStorageItem object) {
        final File file = getFile(object.getStoragePath());
        try {
            if (fileSystemStorageProperties.isMockWrite()) {
                log.info("[pubObject] 模拟写入文件 {}, 跳过物理文件写入", file.getAbsolutePath());
                return object.getStoragePath();
            }
            Files.createParentDirs(file);
            Files.asByteSink(file).writeFrom(stream);

            // 根据配置自动添加文件权限
            autoSetFilePermission(file);
            return object.getStoragePath();
        } catch (IOException ioe) {
            log.error("Failed to write file " + file, ioe);
            return null;
        }
    }

    /**
     * 根据配置自动添加文件权限
     *
     * @param file
     * @throws IOException
     */
    private void autoSetFilePermission(File file) throws IOException {
        OperatingSystem operatingSystem = OperatingSystem.current();
        if (fileSystemStorageProperties.isAutoSetFilePermission()
            && (operatingSystem.isLinux() || operatingSystem.isMacOsX() || operatingSystem.isUnix())) {
            Path parent = Paths.get(file.getParent());
            while (parent != null && !parent.equals(rootPath)) {
                log.debug("Setting parent permission: {}", parent);
                setFilePermission(parent, DIRECTORY_PERMISSIONS);
                parent = parent.getParent();
            }
            Path path = Paths.get(file.getAbsolutePath());
            setFilePermission(path, FILE_PERMISSIONS);
        }
    }

    @Override
    public InputStream getObject(ObjectStorageItem object) {
        final File file = getFile(object.getStoragePath());
        try {
            return new AutoCloseInputStream(Files.asByteSource(file).openStream());
        } catch (FileNotFoundException fileNotFoundException) {
            throw new ObjectStorageItemNotFoundException(fileNotFoundException, object);
        } catch (IOException ioe) {
            log.error("Failed to read file " + file, ioe);
            return null;
        }
    }

    @Override
    public Map<String, String> getDirectUploadingPolicy(ObjectStorageItem object, String prefix) {
        Map<String, String> map = Maps.newHashMap();
        map.put(UploadingPolicyKeys.DIR, prefix);
        map.put(UploadingPolicyKeys.HOST, fileSystemStorageProperties.getDirectUploadApi());
        map.put(UploadingPolicyKeys.RESOURCE, object.getResourceConfig());
        return map;
    }

    @Override
    public String getObjectAccessUrl(ObjectStorageItem object, ObjectStorageItemAccessOptions options) {
        if (fileSystemStorageProperties.isUseFilePathUrl()) {
            return getFile(object.getStoragePath()).getAbsolutePath();
        } else {
            String accessUrlFormat = null;

            if (ObjectStorageType.PUBLIC_RESOURCE.equals(object.getType())) {
                accessUrlFormat = fileSystemStorageProperties.getPublicAccessUrl();
            } else {
                accessUrlFormat = fileSystemStorageProperties.getPrivateAccessUrl();
            }

            if (StringUtils.isBlank(accessUrlFormat)) {
                log.error("无法访问url, 未配置accessUrlFormat: {}", object);
                throw new ObjectStorageException("Failed to get access url because access url format is not configured", object);
            } else {
                return String.format(accessUrlFormat, object.getStoragePath());
            }
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param object
     * @return
     */
    @Override
    public Boolean doesObjectExist(ObjectStorageItem object) {
        return getFile(object.getStoragePath()).exists();
    }

    @Override
    public void delete(ObjectStorageItem item) {
        File file = getFile(item.getPath());
        try {
            FileUtils.delete(file);
        } catch (Exception e) {
            log.error("本地文件删除失败:{}", e);
        }
    }

    private void setFilePermission(Path path, Collection<PosixFilePermission> permissions) throws IOException {
        Set<PosixFilePermission> posixFilePermissions = java.nio.file.Files.getPosixFilePermissions(path);
        if (posixFilePermissions.containsAll(permissions)) {
            return;
        }
        posixFilePermissions.addAll(permissions);
        java.nio.file.Files.setPosixFilePermissions(path, posixFilePermissions);
    }

    @Override
    public Instant getLastModifiedDate(ObjectStorageItem object) {
        File file = getFile(object.getStoragePath());
        return Instant.ofEpochMilli(file.lastModified());
    }

    /**
     * 追加存储，返回追加结果
     *
     * @param object   存储对象信息
     * @param bytes    追加信息
     * @param position 追加位置
     * @return
     */
    @Override
    public ObjectAppendResult appendObject(ObjectStorageItem object, byte[] bytes, Long position) {
        final File file = getFile(object.getStoragePath());

        synchronized (file.getPath()) {
            boolean exists = file.exists();
            try {
                if (fileSystemStorageProperties.isMockWrite()) {
                    log.info("[pubObject] 模拟写入文件 {}, 跳过物理文件写入", file.getAbsolutePath());
                    return new ObjectAppendResult(object.getStoragePath(), 1L);
                }
                Files.createParentDirs(file);
                Files.asByteSink(file, FileWriteMode.APPEND).write(bytes);

                if (!exists) {
                    autoSetFilePermission(file);
                }
                return new ObjectAppendResult(object.getStoragePath(), 1L);
            } catch (IOException ioe) {
                log.error("Failed to write file " + file, ioe);
                return null;
            }

        }
    }

    @Override
    public Map<String, String> getTemporaryAccessToken(ObjectStorageResourceProperties resourceConfig) {
        // 本地文件系统直接返回空参数
        return Maps.newHashMap();
    }
}
