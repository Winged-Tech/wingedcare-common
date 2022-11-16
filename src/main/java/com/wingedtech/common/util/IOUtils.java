package com.wingedtech.common.util;

import java.io.Closeable;

/**
 * IO related util methods.
 * @author taozhou
 */
public final class IOUtils {
    /**
     * Safely close an instance of Closable
     * @param closeable
     */
    public static void safeClose(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        }
        catch (Exception e) {}
    }
}
