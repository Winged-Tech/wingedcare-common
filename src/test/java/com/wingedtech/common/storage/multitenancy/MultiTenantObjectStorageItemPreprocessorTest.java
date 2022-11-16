package com.wingedtech.common.storage.multitenancy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author taozhou
 * @date 2020/10/12
 */
public class MultiTenantObjectStorageItemPreprocessorTest {

    @Test
    public void parseTenantIdFromPath() {
        assertThat(MultiTenantObjectStorageItemPreprocessor.parseTenantIdFromPath("tenant1/object1/file1.txt", "/")).isEqualTo("tenant1");
        assertThat(MultiTenantObjectStorageItemPreprocessor.parseTenantIdFromPath("/tenant1/object1/file1.txt", "/")).isEqualTo("tenant1");
        assertThat(MultiTenantObjectStorageItemPreprocessor.parseTenantIdFromPath("object1/file1.txt", "/")).isEqualTo("object1");
        assertThat(MultiTenantObjectStorageItemPreprocessor.parseTenantIdFromPath("object1", "/")).isNull();
        assertThat(MultiTenantObjectStorageItemPreprocessor.parseTenantIdFromPath("/object1", "/")).isNull();
    }
}
