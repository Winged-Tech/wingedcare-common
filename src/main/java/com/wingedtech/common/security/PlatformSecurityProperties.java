package com.wingedtech.common.security;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "platform")
public class PlatformSecurityProperties {

    @Getter
    private Token token = new Token();

    @Data
    public static class Token {
        /**
         * 默认不开启压缩
         */
        private boolean enabledCompress = false;

        /**
         * 默认使用jwt token store
         */
        private String store = "jwt";
    }

    public boolean isEnableJwtTokenStore() {
        return "jwt".equals(token.store);
    }
}
