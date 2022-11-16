package com.wingedtech.common.storage.errors;

import com.wingedtech.common.storage.ObjectStorageItem;

public class ObjectStorageException extends RuntimeException {

    private final ObjectStorageItem item;

    public ObjectStorageException(ObjectStorageItem item) {
        this.item = item;
    }

    public ObjectStorageException(String message, ObjectStorageItem item) {
        super(message);
        this.item = item;
    }

    public ObjectStorageException(String message, Throwable cause, ObjectStorageItem item) {
        super(message, cause);
        this.item = item;
    }

    public ObjectStorageException(Throwable cause, ObjectStorageItem item) {
        super(cause);
        this.item = item;
    }

    public ObjectStorageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ObjectStorageItem item) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.item = item;
    }
}
