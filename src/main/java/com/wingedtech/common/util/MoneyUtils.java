package com.wingedtech.common.util;

/**
 * TODO description
 *
 * @author Jason
 * @since 2019-05-05 16:04
 */
public class MoneyUtils {

    /**
     * A dime is transformed into a dollar money
     *
     * @param amount
     * @return
     */
    public static String pennyConvertingYuan(Long amount) {
        if (amount == null) {
            return "0.00";
        }
        long absAmount = Math.abs(amount);
        if (amount < 0) {
            return "-".concat(getYuan(absAmount));
        }
        return getYuan(absAmount);
    }

    private static String getYuan(long absAmount) {
        if (absAmount < 10) {
            return "0.0".concat(String.valueOf(absAmount));
        }
        if (absAmount < 100) {
            return "0.".concat(String.valueOf(absAmount));
        }
        if (absAmount == 100) {
            return "1.00";
        }
        String strYuan = String.valueOf(absAmount);
        return String.valueOf(absAmount / 100).concat(".").concat(strYuan.substring(strYuan.length() - 2));
    }

}
