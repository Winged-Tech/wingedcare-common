package com.wingedtech.common.platform;

import com.wingedtech.common.constant.ClientType;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.annotation.Annotation;

public class ClientPlatformUtils {
    /**
     * 判断指定的类型是否支持指定的客户端类型。
     * @param clazz 标记有SupportingClientType的类型
     * @param clientType
     * @return
     */
    public static boolean supportsClientType(Class clazz, ClientType clientType) {
        final Annotation[] supporitingClientType = clazz.getAnnotationsByType(SupportingClientType.class);
        return supportsClientType(clientType, supporitingClientType);
    }

    private static boolean supportsClientType(ClientType clientType, Annotation[] supporitingClientType) {
        if (ArrayUtils.isEmpty(supporitingClientType)) {
            throw new IllegalArgumentException("The specified type or field doesn't have any SupportingClientType annotations!");
        }
        for (Annotation annotation : supporitingClientType) {
            if (supportsClientType((SupportingClientType) annotation, clientType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断指定的枚举常量是否支持指定的客户端类型。
     * @param enumValue 标记有SupportingClientType的枚举常量
     * @param clientType
     * @return
     */
    public static <E extends Enum> boolean supportsClientType(E enumValue, ClientType clientType) {
        final SupportingClientType[] supportingClientTypes;
        try {
            supportingClientTypes = enumValue.getClass().getField(enumValue.name()).getAnnotationsByType(SupportingClientType.class);
            return supportsClientType(clientType, supportingClientTypes);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Invalid enum value " + enumValue);
        }
    }

    public static boolean supportsClientType(SupportingClientType annotation, ClientType type) {
        if (annotation.allTypes()) {
            return true;
        }
        if (ArrayUtils.contains(annotation.value(), type)) {
            return true;
        }
        return false;
    }
}
