package com.wingedtech.common.util;

import com.wingedtech.common.multitenancy.Tenant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.core.env.Environment;

import java.util.List;

public class DbMigrationUtils {
    public static final String PROPERTIES_PATH_ROOT = "winged.dbmigrations.";

    /**
     * 从Environment配置中读取指定类型的数据列表
     * @param environment
     * @param resourceName
     * @param clazz
     * @param loadTenantData
     * @param <E>
     * @return
     */
    public static <E> List<E> loadDataListFromEnvironment(Environment environment, String resourceName, Class<E> clazz, boolean loadTenantData) {
        Bindable<List<E>> target = Bindable.listOf(clazz);
        return bindTarget(environment, resourceName, loadTenantData, target);
    }

    /**
     * 从Environment配置中读取指定类型的数据
     * @param environment
     * @param resourceName
     * @param clazz
     * @param loadTenantData
     * @param <E>
     * @return
     */
    public static <E> E loadDataFromEnvironment(Environment environment, String resourceName, Class<E> clazz, boolean loadTenantData) {
        Bindable<E> target = Bindable.of(clazz);
        return bindTarget(environment, resourceName, loadTenantData, target);
    }

    private static <E> E bindTarget(Environment environment, String resourceName, boolean loadTenantData, Bindable<E> target) {
        StringBuilder builder = new StringBuilder(PROPERTIES_PATH_ROOT);
        if (Tenant.isEnabledMultitenancy() && loadTenantData) {
            // Spring property 绑定时, 只允许小写格式的名字, 这里防止tenantId出现大写字母
            // https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config-relaxed-binding
            builder.append(StringUtils.lowerCase(Tenant.getCurrentTenantId()));
        }
        else {
            builder.append("default");
        }
        builder.append(".").append(resourceName);
        String name = builder.toString();
        final ConfigurationPropertyName propertyName = ConfigurationPropertyName.of(name);
        final BindResult<E> bindResult = Binder.get(environment).bind(propertyName, target);
        if (bindResult.isBound()) {
            return bindResult.get();
        } else {
            return null;
        }
    }
}
