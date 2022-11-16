package com.wingedtech.common.util;

import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * 身份证算法工具类
 *
 * @author Jason
 * @since 2019-05-20 17:52
 */
@Getter
@Setter
public class IDCardUtil {

    private String statueMessage;
    private Byte sex;
    private String birthday;
    private Integer age;

    public static IDCardUtil parseIdCardNo(String idCardNo) {

        IDCardUtil resultDTO = new IDCardUtil();
        String myRegExpIDCardNo = "^\\d{6}(((19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])\\d{3}([0-9]|x|X))|(\\d{2}(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])\\d{3}))$";
        boolean valid = Pattern.matches(myRegExpIDCardNo, idCardNo) || (idCardNo.length() == 17 && Pattern.matches(myRegExpIDCardNo, idCardNo.substring(0, 15)));
        if (!valid) {
            resultDTO.setStatueMessage("证件号码不规范!");
            return resultDTO;
        }
        int idxSexStart = 16;
        int birthYearSpan = 4;
        // 如果是15位的证件号码
        if (idCardNo.length() == 15) {
            idxSexStart = 14;
            birthYearSpan = 2;
        }

        // 性别
        String idxSexStr = idCardNo.substring(idxSexStart, idxSexStart + 1);
        int idxSex = Integer.parseInt(idxSexStr) % 2;
        resultDTO.setSex((byte) ((idxSex == 1) ? 1 : 0));

        // 出生日期
        String year = (birthYearSpan == 2 ? "19" : "") + idCardNo.substring(6, 6 + birthYearSpan);
        String month = idCardNo.substring(6 + birthYearSpan, 6 + birthYearSpan + 2);
        String day = idCardNo.substring(8 + birthYearSpan, 8 + birthYearSpan + 2);
        String birthday = year + '-' + month + '-' + day;
        resultDTO.setBirthday(birthday);

        // 年龄
        Calendar idCardCal = Calendar.getInstance();
        Calendar currentTimeCal = Calendar.getInstance();
        idCardCal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
        Integer yearAge = (currentTimeCal.get(Calendar.YEAR)) - (idCardCal.get(Calendar.YEAR));
        idCardCal.set(currentTimeCal.get(Calendar.YEAR), Integer.parseInt(month) - 1, Integer.parseInt(day));
        Integer monthFloor = (currentTimeCal.before(idCardCal) ? 1 : 0);
        resultDTO.setAge(yearAge - monthFloor);

        return resultDTO;
    }
}
