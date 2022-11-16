package com.wingedtech.common.sensitivity;

import com.wingedtech.common.security.SecurityUtils;
import com.wingedtech.common.sensitivity.annotation.SensitivityMark;
import com.wingedtech.common.sensitivity.annotation.SensitivityProperty;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author 6688SUN
 */
public final class SensitivityUtils {

    public static boolean mustDesensitization() {
        return !SecurityUtils.isCurrentUserInRole("PERM_RMG");
    }

    public static void desensitizer(@Nullable Object source) {
        if (source == null) {
            return;
        }
        if (source instanceof Collection) {
            Collection<?> bodyCollection = (Collection<?>) source;
            bodyCollection.forEach(SensitivityUtils::invoke);
        } else {
            SensitivityUtils.invoke(source);
        }
    }

    private static void invoke(@Nullable Object body) {
        if (body == null) {
            return;
        }
        final Class<?> bodyClass = body.getClass();
        if (bodyClass == null) {
            return;
        }

        ReflectionUtils.doWithFields(bodyClass, field -> {
            field.setAccessible(true);
            SensitivityMark mark = field.getAnnotation(SensitivityMark.class);
            if (mark != null) {
                final Object markBody = field.get(body);
                desensitizer(markBody);
            } else {
                SensitivityProperty property = field.getAnnotation(SensitivityProperty.class);
                if (property != null) {
                    final Object propertyBody = field.get(body);
                    if (propertyBody != null) {
                        try {
                            if (property.useCustom()) {
                                field.set(body, property.custom().newInstance().desensitizer(propertyBody));
                            } else {
                                field.set(body, property.strategy().desensitizer().apply(propertyBody));
                            }
                        } catch (InstantiationException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
    }

    private SensitivityUtils() {

    }
}
