package com.wingedtech.common.storage.store;

import com.wingedtech.common.service.GenericServiceTemplate;
import com.wingedtech.common.service.beloging.ObjectWithUserIdServiceTemplate;
import com.wingedtech.common.storage.ObjectStorageItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

public interface ObjectStorageItemStore  {

    Optional<ObjectStorageItem> findByPath(@NotBlank String path);

    Optional<ObjectStorageItem> findByPathForCurrentUser(@NotBlank String path);

    ObjectStorageItem store(ObjectStorageItem item);

    boolean existsByPath(@NotBlank String path);

    boolean existsByResourceAndName(@NotBlank String resource, @NotBlank String name, String objectId);

    Optional<ObjectStorageItem> findByResourceAndName(@NotBlank String resource, @NotBlank String name, String objectId);

    Optional<ObjectStorageItem> findUniqueItem(@NotNull ObjectStorageItem item);

    Page<ObjectStorageItem> findAllForCurrentUserByExample(ObjectStorageItem example, Pageable pageableParameter);

    Optional<ObjectStorageItem> findOne(String id);

    void delete(String id);

    Optional<ObjectStorageItem> findOneForCurrentUser(String id);
}
