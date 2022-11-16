package com.wingedtech.common.util;

import com.wingedtech.common.codec.EncodeUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

public class HeaderValuesUtils {

    /**
     * 根据不同浏览器将文件名中的汉字转为UTF8编码的串,以便下载时能正确显示另存的文件名.
     *
     * @param request
     * @param filename
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String contentDispositionValues(HttpServletRequest request, String filename) throws UnsupportedEncodingException {
        return "attachment; filename=".concat(EncodeUtils.toUtf8String(request, filename));
    }

    public static String contentDispositionInline(HttpServletRequest request, String filename) throws UnsupportedEncodingException {
        return "inline; filename=".concat(EncodeUtils.toUtf8String(request, filename));
    }
}
