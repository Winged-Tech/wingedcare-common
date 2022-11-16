package com.wingedtech.common.storage.inspur;

import com.inspurcloud.oss.exception.OSSException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author taozhou
 * @date 2020/12/22
 */
public class InspurFileStorageTestUtils {

    public static RequestBody createRequestBodyFromInputStream(final InputStream inputStream) {
        return new RequestBody() {
            @Nullable
            public MediaType contentType() {
                return MediaType.parse("application");
            }

            public long contentLength() {
                try {
                    int available = inputStream.available();
                    return available <= 2147483646 ? (long)available : -1L;
                } catch (IOException var2) {
                    throw new OSSException(var2.getMessage(), var2);
                }
            }

            public void writeTo(BufferedSink sink) throws IOException {
                Source source = Okio.source(inputStream);
                Throwable var3 = null;

                try {
                    sink.writeAll(source);
                } catch (Throwable var12) {
                    var3 = var12;
                    throw var12;
                } finally {
                    if (source != null) {
                        if (var3 != null) {
                            try {
                                source.close();
                            } catch (Throwable var11) {
                                var3.addSuppressed(var11);
                            }
                        } else {
                            source.close();
                        }
                    }

                }

            }
        };
    }
}
