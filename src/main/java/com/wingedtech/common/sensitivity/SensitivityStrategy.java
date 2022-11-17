package com.wingedtech.common.sensitivity;

import java.util.function.Function;

/**
 * @author 6688SUN
 */
public enum SensitivityStrategy {

    /**
     * 姓名
     */
    USERNAME(s -> {
        char[] str = String.valueOf(s).toCharArray();
        if (str.length == 2) {
            return "*" + str[1];
        } else if (str.length > 2) {
            for (int i = 1; i < str.length - 1; i++) {
                str[i] = '*';
            }
            return String.valueOf(str);
        } else {
            return s;
        }
    }),

    /**
     * 手机号
     */
    PHONE(s -> String.valueOf(s).replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")),

    /**
     * 地址
     */
    ADDRESS(s -> String.valueOf(s).replaceAll("(\\S{3})\\S{2}(\\S*)\\S{2}", "$1****$2****")),

    /**
     * 证件号
     */
    CERTIFICATE_NUMBER(sObj -> {
        final String s = String.valueOf(sObj);
        int length = s.length();
        String number;
        switch (length) {
            case 9:
                number = s.replaceAll("(\\w{2})\\w*(\\w{3})", "$1****$2");
                break;
            case 15:
                number = s.replaceAll("(\\w{2})\\w*(\\w{2})", "$1***********$2");
                break;
            case 18:
                number = s.replaceAll("(\\w{2})\\w*(\\w{2})", "$1**************$2");
                break;
            default:
                number = s.replaceAll("(\\w{1})\\w*(\\w{1})", "$1***$2");
                break;
        }
        return number;
    });


    private final Function<Object, Object> desensitizer;

    SensitivityStrategy(Function<Object, Object> desensitizer) {
        this.desensitizer = desensitizer;
    }

    public Function<Object, Object> desensitizer() {
        return desensitizer;
    }

}
