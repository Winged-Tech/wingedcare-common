package com.wingedtech.common.security.oauth2.token;

import com.wingedtech.common.security.ExtensionOauth2Constants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.*;

/**
 * @author 6688 Sun
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ConditionalOnProperty(value = ExtensionOauth2Constants.PLATFORM_TOKEN_STORE, havingValue = TokenStores.JDBC)
public @interface ConditionalOnJdbcTokenStore {
}
