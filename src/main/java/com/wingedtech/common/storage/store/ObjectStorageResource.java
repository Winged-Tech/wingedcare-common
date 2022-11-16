package com.wingedtech.common.storage.store;

import com.wingedtech.common.constant.Requests;
import com.wingedtech.common.errors.BusinessException;
import com.wingedtech.common.storage.ObjectStorageItem;
import com.wingedtech.common.storage.ObjectStorageService;
import com.wingedtech.common.storage.rest.StorageResource;
import com.wingedtech.common.util.ExampleQueryVM;
import com.wingedtech.common.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * 供用户查询并访问OSS文件的接口
 */
@RestController
@RequestMapping(Requests.API_USER + "/oss/items")
@Slf4j
public class ObjectStorageResource extends StorageResource {
    private final ObjectStorageItemStore storageItemStore;

    public ObjectStorageResource(ObjectStorageService objectStorageService, ObjectStorageItemStore storageItemStore) {
        super(objectStorageService);
        this.storageItemStore = storageItemStore;
    }

    @PostMapping("/by-example")
    @ApiOperation("使用example对象为当前用户查询存储项列表")
    public ResponseEntity<List<ObjectStorageItem>> getUserItemsByExample(@RequestBody ExampleQueryVM<ObjectStorageItem> query) {
        final Page<ObjectStorageItem> page = storageItemStore.findAllForCurrentUserByExample(query.getExample(), query.getPageableParameter());
        return ResponseEntity.ok()
            .headers(PaginationUtil.generatePaginationHttpHeaders(page, "/api/user/oss/items"))
            .body(page.getContent());
    }

    @GetMapping("/delete/{id}")
    @ApiOperation("通过id删除指定记录接口")
    public ResponseEntity<Void> delete(@PathVariable(value = "id") String id) {
        Optional<ObjectStorageItem> one = storageItemStore.findOne(id);
        if (one.isPresent()) {
            storageItemStore.delete(id);
        } else {
            throw new BusinessException("文件不存在！");
        }
        return null;
    }

    @GetMapping("/{id}")
    @ApiOperation("使用id为当前用户获取一条存储项详情")
    public ResponseEntity<ObjectStorageItem> getUserItemById(@NotBlank @PathVariable("id") String id) {
        return ResponseUtil.wrapOrNotFound(storageItemStore.findOneForCurrentUser(id));
    }

    @GetMapping("/download/{id}")
    @ApiOperation("使用id为当前用户重定向下载指定文件")
    public ResponseEntity<String> redirectAndDownload(@NotBlank @PathVariable("id") String id) throws URISyntaxException {
        return redirectToItem(id, true);
    }

    private ResponseEntity<String> redirectToItem(@PathVariable("id") @NotBlank String id, boolean download) throws URISyntaxException {
        final ObjectStorageItem item = getItemAndCheckState(id);

        return super.redirectToResource(item.getStoragePath(), item.getResourceConfig(), item.getName(), download);
    }

    private ObjectStorageItem getItemAndCheckState(@PathVariable("id") @NotBlank String id) {
        final ObjectStorageItem item = storageItemStore.findOneForCurrentUser(id).orElseThrow(() -> new BusinessException("找不到指定的文件"));
        if (!item.isInStorage()) {
            throw new BusinessException("该文件还未完成存储!");
        }
        return item;
    }

    @GetMapping("/inline/{id}")
    @ApiOperation("使用id为当前用户重定向到指定文件(内联方式)")
    public ResponseEntity<String> redirectInline(@NotBlank @PathVariable("id") String id) throws URISyntaxException {
        return redirectToItem(id, false);
    }

    @GetMapping("/url/{id}")
    @ApiOperation("获取指定文件的最终访问url")
    public ResponseEntity<String> getItemAccessUrl(@NotBlank @PathVariable("id") String id, @RequestParam(name = "download", defaultValue = "true") Boolean download) {
        final ObjectStorageItem item = getItemAndCheckState(id);
        return ResponseEntity.ok(super.getRedirectUrl(item.getStoragePath(), item.getResourceConfig(), item.getName(), BooleanUtils.isNotFalse(download), null));
    }
}
