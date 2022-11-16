package com.wingedtech.common.util;

import com.google.common.collect.Lists;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FileUtils {
    public static final int WINDOWS_PATH_LIMIT = 260;
    private static final Comparator<File> FILE_SEGMENT_COMPARATOR = new Comparator<File>() {
        public int compare(File left, File right) {
            String leftPath = left.getPath();
            String rightPath = right.getPath();
            int len1 = leftPath.length();
            int len2 = rightPath.length();
            int lim = Math.min(len1, len2);

            for (int k = 0; k < lim; ++k) {
                char c1 = leftPath.charAt(k);
                char c2 = rightPath.charAt(k);
                if (c1 != c2) {
                    if (c1 == File.separatorChar) {
                        return -1;
                    }

                    if (c2 == File.separatorChar) {
                        return 1;
                    }

                    return c1 - c2;
                }
            }

            return len1 - len2;
        }
    };

    public FileUtils() {
    }

    public static String toSafeFileName(String name) {
        int size = name.length();
        StringBuffer rc = new StringBuffer(size * 2);

        for (int i = 0; i < size; ++i) {
            char c = name.charAt(i);
            boolean valid = c >= 'a' && c <= 'z';
            valid = valid || c >= 'A' && c <= 'Z';
            valid = valid || c >= '0' && c <= '9';
            valid = valid || c == '_' || c == '-' || c == '.' || c == '$';
            if (valid) {
                rc.append(c);
            } else {
                rc.append('#');
                rc.append(Integer.toHexString(c));
            }
        }

        return rc.toString();
    }

    public static File assertInWindowsPathLengthLimitation(File file) {
        if (file.getAbsolutePath().length() > 260) {
            throw new RuntimeException(String.format("Cannot create file. '%s' exceeds windows path limitation of %d character.", file.getAbsolutePath(), 260));
        } else {
            return file;
        }
    }

    public static Collection<? extends File> calculateRoots(Iterable<? extends File> files) {
        List<File> sortedFiles = Lists.newArrayList(files);
        Collections.sort(sortedFiles, FILE_SEGMENT_COMPARATOR);
        List<File> result = Lists.newArrayListWithExpectedSize(sortedFiles.size());
        File currentRoot = null;
        Iterator var4 = sortedFiles.iterator();

        while (true) {
            File file;
            do {
                if (!var4.hasNext()) {
                    return result;
                }

                file = (File) var4.next();
            } while (currentRoot != null && doesPathStartWith(file.getPath(), currentRoot.getPath()));

            result.add(file);
            currentRoot = file;
        }
    }

    public static boolean doesPathStartWith(String path, String startsWithPath) {
        if (!path.startsWith(startsWithPath)) {
            return false;
        } else {
            return path.length() == startsWithPath.length() || path.charAt(startsWithPath.length()) == File.separatorChar;
        }
    }

    public static boolean hasExtension(File file, String extension) {
        return file.getPath().endsWith(extension);
    }

    public static boolean hasExtensionIgnoresCase(String fileName, String extension) {
        return endsWithIgnoreCase(fileName, extension);
    }

    private static boolean endsWithIgnoreCase(String subject, String suffix) {
        return subject.regionMatches(true, subject.length() - suffix.length(), suffix, 0, suffix.length());
    }

    public static String withExtension(String filePath, String extension) {
        return filePath.toLowerCase().endsWith(extension) ? filePath : removeExtension(filePath) + extension;
    }

    public static String removeExtension(String filePath) {
        int fileNameStart = Math.max(filePath.lastIndexOf(47), filePath.lastIndexOf(92));
        int extensionPos = filePath.lastIndexOf(46);
        return extensionPos > fileNameStart ? filePath.substring(0, extensionPos) : filePath;
    }

    public static File canonicalize(File src) {
        try {
            return src.getCanonicalFile();
        } catch (IOException var2) {
            throw new UncheckedIOException(var2);
        }
    }

    public static File normalize(File src) {
        String path = src.getAbsolutePath();
        String normalizedPath = FilenameUtils.normalize(path);
        if (normalizedPath != null) {
            return new File(normalizedPath);
        } else {
            File root = src;

            for (File parent = src.getParentFile(); parent != null; parent = root.getParentFile()) {
                root = root.getParentFile();
            }

            return root;
        }
    }
}
