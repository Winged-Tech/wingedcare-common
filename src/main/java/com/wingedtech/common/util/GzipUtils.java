package com.wingedtech.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.springframework.util.FastByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;


@Slf4j
public class GzipUtils {

    public static final String GZIP_ENCODE_UTF_8 = "UTF-8";
    public static final String GZIP_ENCODE_ISO_8859_1 = "ISO-8859-1";


    public static byte[] compress(String str, String encoding) {
        if (str == null || str.length() == 0) {
            return null;
        }
        FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        try (GzipCompressorOutputStream gzip = new GzipCompressorOutputStream(out)) {
            gzip.write(str.getBytes(encoding));
        } catch (Exception e) {
            log.error("Gzip 压缩异常: ", e);
        }
        return out.toByteArray();
    }

    public static byte[] compress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        try (GzipCompressorOutputStream gzip = new GzipCompressorOutputStream(out)) {
            gzip.write(bytes);
        } catch (Exception e) {
            log.error("Gzip 压缩异常: ", e);
        }
        return out.toByteArray();
    }

    public static byte[] compress(String str) {
        return compress(str, GZIP_ENCODE_UTF_8);
    }

    public static byte[] uncompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try (GzipCompressorInputStream ungzip = new GzipCompressorInputStream(in)) {
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (Exception e) {
            log.error("Gzip 解压异常: ", e);
        }
        return out.toByteArray();
    }

    public static String uncompressToString(byte[] bytes, String encoding) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try (GzipCompressorInputStream ungzip = new GzipCompressorInputStream(in)) {
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toString(encoding);
        } catch (Exception e) {
            log.error("Gzip 解压异常: ", e);
        }
        return null;
    }

    public static String uncompressToString(byte[] bytes) {
        return uncompressToString(bytes, GZIP_ENCODE_UTF_8);
    }

    public static void main(String[] args) {

        String s = "PERM_DATA_IMPORT_104_10";
        System.out.println("字符串长度：" + s.length());
        byte[] compress = compress(s);
        System.out.println("压缩后：：" + compress.length);
        System.out.println("解压后：" + uncompress(compress).length);
        String re = uncompressToString(compress);
        System.out.println("解压字符串后：：" + re.length());

        System.out.println(Arrays.toString(compress));
        System.out.println("解压后字符串：" + re);
    }
}
