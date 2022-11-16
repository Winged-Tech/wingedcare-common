package com.wingedtech.common.platform;

import com.wingedtech.common.constant.ClientType;
import org.apache.commons.lang3.EnumUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ClientPlatformUtilsTest {

    @Test
    public void supportsClientTypeByClass() {
        assertThat(ClientPlatformUtils.supportsClientType(TestTypeWechat.class, ClientType.PC_WEB)).isTrue();
        assertThat(ClientPlatformUtils.supportsClientType(TestTypeWechat.class, ClientType.WECHAT_WEB)).isTrue();
        assertThat(ClientPlatformUtils.supportsClientType(TestTypeWechat.class, ClientType.WECHAT_MA)).isFalse();
        assertThat(ClientPlatformUtils.supportsClientType(TestTypeWechat.class, ClientType.APP_IOS)).isFalse();
        assertThat(ClientPlatformUtils.supportsClientType(TestTypeWechat.class, ClientType.APP_ANDROID)).isFalse();

        final List<ClientType> enumList = EnumUtils.getEnumList(ClientType.class);
        for (ClientType clientType : enumList) {
            assertThat(ClientPlatformUtils.supportsClientType(TestTypeAll.class, clientType)).describedAs("TestTypeWechat %s 测试失败", clientType).isTrue();
        }
    }


    @Test
    public void supportsClientTypeByEnum() {
        assertThat(ClientPlatformUtils.supportsClientType(TestEnumWechat.TEST, ClientType.PC_WEB)).isTrue();
        assertThat(ClientPlatformUtils.supportsClientType(TestEnumWechat.TEST, ClientType.WECHAT_WEB)).isTrue();
        assertThat(ClientPlatformUtils.supportsClientType(TestEnumWechat.TEST, ClientType.WECHAT_MA)).isFalse();
        assertThat(ClientPlatformUtils.supportsClientType(TestEnumWechat.TEST, ClientType.APP_IOS)).isFalse();
        assertThat(ClientPlatformUtils.supportsClientType(TestEnumWechat.TEST, ClientType.APP_ANDROID)).isFalse();

        final List<ClientType> enumList = EnumUtils.getEnumList(ClientType.class);
        for (ClientType clientType : enumList) {
            assertThat(ClientPlatformUtils.supportsClientType(TestEnumWechat.ALL, clientType)).describedAs("TestEnumWechat.ALL %s 测试失败", clientType).isTrue();
        }
    }

    @SupportingClientType({ClientType.WECHAT_WEB, ClientType.PC_WEB})
    static class TestTypeWechat {}

    @SupportingClientType(allTypes = true)
    static class TestTypeAll {}

    enum TestEnumWechat {
        @SupportingClientType({ClientType.WECHAT_WEB, ClientType.PC_WEB})
        TEST,
        @SupportingClientType(allTypes = true)
        ALL
    }
}
