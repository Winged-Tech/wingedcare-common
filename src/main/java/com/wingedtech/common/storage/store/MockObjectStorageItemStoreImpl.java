package com.wingedtech.common.storage.store;

import com.wingedtech.common.storage.ObjectStorageItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

public class MockObjectStorageItemStoreImpl implements ObjectStorageItemStore {
    @Override
    public Optional<ObjectStorageItem> findByPath(@NotBlank String path) {
        return Optional.empty();
    }

    @Override
    public Optional<ObjectStorageItem> findByPathForCurrentUser(@NotBlank String path) {
        return Optional.empty();
    }

    @Override
    public ObjectStorageItem store(ObjectStorageItem item) {
        return null;
    }

    @Override
    public boolean existsByPath(@NotBlank String path) {
        return false;
    }

    @Override
    public boolean existsByResourceAndName(@NotBlank String resource, @NotBlank String name, String objectId) {
        return false;
    }

    @Override
    public Optional<ObjectStorageItem> findByResourceAndName(@NotBlank String resource, @NotBlank String name, String objectId) {
        return Optional.empty();
    }

    @Override
    public Optional<ObjectStorageItem> findUniqueItem(@NotNull ObjectStorageItem item) {
        return Optional.empty();
    }

    @Override
    public Page<ObjectStorageItem> findAllForCurrentUserByExample(ObjectStorageItem example, Pageable pageableParameter) {
        return null;
    }

    @Override
    public Optional<ObjectStorageItem> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public Optional<ObjectStorageItem> findOneForCurrentUser(String id) {
        return Optional.empty();
    }
}
