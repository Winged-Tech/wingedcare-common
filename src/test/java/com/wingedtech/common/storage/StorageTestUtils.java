package com.wingedtech.common.storage;

import org.apache.commons.io.FileUtils;

import java.io.*;

public class StorageTestUtils {
    private static final String ENCODING = "UTF-8";

    public static InputStream createTempFileStream(String content) throws IOException {
        File file = createTempFile(content);
        return new FileInputStream(file);
    }

    public static File createTempFile(String content) throws IOException {
        File tempFile = File.createTempFile("test", "storage");
        tempFile.deleteOnExit();
        FileUtils.write(tempFile, content, ENCODING);
        return tempFile;
    }

    public static String readStreamContentOneLine(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.readLine();
    }
}
