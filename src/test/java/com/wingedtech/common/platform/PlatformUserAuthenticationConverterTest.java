package com.wingedtech.common.platform;

import com.google.common.collect.Lists;
import com.wingedtech.common.security.PlatformUserAuthenticationConverter;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.jwt.codec.Codecs;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class PlatformUserAuthenticationConverterTest {

    PlatformUserAuthenticationConverter platformUserAuthenticationConverter = new PlatformUserAuthenticationConverter();


    @Test
    public void convertUserAuthenticationTest() {

        List<String> authorities = Lists.newArrayList("02_210_04", "02_210_05", "PERM_DIS_30_02");

        Authentication authentication = createAuthenticationWithAuthorities(authorities);

        Map<String, ?> tokne = platformUserAuthenticationConverter.convertUserAuthentication(authentication);

        platformUserAuthenticationConverter.extractAuthentication(tokne);
    }

    @Test
    public void test() {
        testAuthorities(Lists.newArrayList("02_210_04", "02_210_05", "PERM_DIS_30_02"));
    }

    /**
     * 超级管理员权限测试
     */
    @Test
    public void testAdmin() {
        testAuthorities(Lists.newArrayList("ROLE_SYS_USER", "01_104_19", "02_240_01", "02_240_00", "01_104_11", "01_104_14", "01_104_13", "02_240_03", "01_104_16", "02_240_02", "02_240_05", "02_240_04", "01_104_17", "02_420_01", "02_420_00", "02_420_05", "02_420_04", "02_420_03", "01_104_10", "02_420_02", "02_140_02", "02_140_01", "02_140_03", "01_101_17", "01_101_16", "02_320_01", "02_320_00", "01_101_15", "01_101_14", "01_101_13", "01_101_12", "01_101_11", "01_101_10", "02_140_00", "02_120_03", "02_120_04", "02_120_05", "02_210_07", "02_210_06", "02_210_05", "02_210_00", "01_102_10", "01_102_11", "01_102_12", "02_210_04", "01_102_13", "02_210_03", "01_102_14", "02_210_02", "02_210_01", "02_120_00", "02_120_01", "02_120_02", "01_104", "01_103", "01_102", "01_101", "01_100", "02_230_01", "02_230_00", "02_530_00", "01_104_20", "01_105", "02_330_00", "02_330_01", "02_410_04", "02_410_03", "02_410_05", "02_410_00", "02_410_02", "02_410_01", "02_510_00", "01_100_12", "01_100_11", "01_100_10", "02_000", "02_150_03", "02_130_00", "02_130_01", "02_150_02", "02_150_01", "02_150_00", "02_130_04", "02_130_02", "02_130_03", "02_310_00", "02_110_02", "02_110_03", "02_110_00", "02_110_01", "02_110_04", "02_520_00", "02_220_01", "02_220_00", "02_220_03", "02_220_02", "01_103_18", "01_103_10", "01_103_11", "01_103_12", "01_103_13", "01_103_14", "01_103_15", "01_103_16", "01_103_17", "02_220_05", "02_220_04", "02_220_07", "02_220_06"));
    }

    @Test
    public void testInformationAdmin() {
        testAuthorities(Lists.newArrayList("ROLE_SYS_USER", "01_104_19","01_102_10","01_104_11","01_102_11","01_104_14","01_102_12","01_104_13","01_102_13","01_104_16","01_102_14","01_104_17","01_100_12","01_100_11","01_100_10","01_104_10","01_101_17","01_101_16","01_103_18","01_103_10","01_104","01_103","01_103_11","01_102","01_103_12","01_101","01_103_13","01_100","01_103_14","01_103_15","01_103_16","01_103_17","01_101_15","01_101_14","01_101_13","01_101_12","01_101_11","01_101_10","01_104_20","01_105"));
    }

    @Test
    public void testOperationAdmin() {
        testAuthorities(Lists.newArrayList("ROLE_SYS_USER", "01_103_18", "01_104_19", "01_103_10", "01_104", "01_103", "01_103_11", "01_104_11", "01_103_12", "01_104_14", "01_103_13", "01_104_13", "01_103_14", "01_104_16", "01_103_15", "01_103_16", "01_103_17", "01_104_17", "01_104_10", "01_104_20"));
    }

    /**
     * 人卡管理员测试
     */
    @Test
    public void testCardAdmin() {
        testAuthorities(Lists.newArrayList("ROLE_SYS_USER", "01_102_10", "01_102", "01_102_11", "01_102_12", "01_102_13", "01_102_14"));
    }

    /**
     * 普通雇员测试
     */
    @Test
    public void testNormalEmployee() {
        testAuthorities(Lists.newArrayList("ROLE_SYS_USER"));
    }
    private void testAuthorities(List<String> authorities) {
        String compressedBase64 = PlatformUserAuthenticationConverter.getCompressedAuthorities(createAuthenticationWithAuthorities(authorities));
        String jsonSerialized = JsonParserFactory.create().formatMap(Maps.newHashMap("authorities", authorities));
//        String withoutCompression = Codecs.utf8Decode(Codecs.utf8Encode(jsonSerialized));
        String withoutCompression = jsonSerialized;

        log.info("Compressed base64: {} - {}", compressedBase64.length(), compressedBase64);
        log.info("Without compression: {} - {}", withoutCompression.length(), withoutCompression);
        assertThat(compressedBase64.length() < withoutCompression.length()).withFailMessage("%s: 压缩后的base64[%s]大于压缩前的序列化大小[%s]", withoutCompression, compressedBase64.length(), withoutCompression.length()).isTrue();
    }

    private Authentication createAuthenticationWithAuthorities(List<String> authorities) {
        return new Authentication() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    Set<SimpleGrantedAuthority> set = new HashSet<>(authorities.size());
                    for (String authority : authorities) {
                        set.add(new SimpleGrantedAuthority(authority));
                    }
                    return set;
                }

                @Override
                public Object getCredentials() {
                    return null;
                }

                @Override
                public Object getDetails() {
                    return null;
                }

                @Override
                public Object getPrincipal() {
                    return null;
                }

                @Override
                public boolean isAuthenticated() {
                    return false;
                }

                @Override
                public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

                }

                @Override
                public String getName() {
                    return "qingxun";
                }
            };
    }
}
