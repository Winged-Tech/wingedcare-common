package com.wingedtech.common.storage.errors;

import com.wingedtech.common.storage.ObjectStorageItem;

public class ObjectStorageItemNotFoundException extends ObjectStorageException {

    public ObjectStorageItemNotFoundException(ObjectStorageItem item) {
        super(item);
    }

    public ObjectStorageItemNotFoundException(String message, ObjectStorageItem item) {
        super(message, item);
    }

    public ObjectStorageItemNotFoundException(String message, Throwable cause, ObjectStorageItem item) {
        super(message, cause, item);
    }

    public ObjectStorageItemNotFoundException(Throwable cause, ObjectStorageItem item) {
        super(cause, item);
    }

    public ObjectStorageItemNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ObjectStorageItem item) {
        super(message, cause, enableSuppression, writableStackTrace, item);
    }
}
