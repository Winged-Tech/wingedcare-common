package com.wingedtech.common.util;

import java.util.Random;
import java.util.zip.CRC32;

/**
 * 邀请码生成器，基本原理：<br/>
 * 入参用户ID：1 <br/>
 * 使用自定义进制转换之后为：V <br/>
 * 转换未字符串，并在后面添加'A'：VA <br/>
 * 在VA后面再随机补足4位，得到：VAHKHE <br/>
 * 反向转换时以'A'为分界线，'A'后面的不再解析 <br/>
 *
 * @author Jason
 * @since 2019-03-27 09:50
 */
public class InvitationCodeUtils {

    /**
     * 自定义进制(0,1没有加入,容易与o,l混淆)，数组顺序可进行调整增加反推难度，A用来补位因此此数组不包含A，共31个字符。
     */
    private static final char[] BASE = new char[]{'H', 'V', 'E', '8', 'S', '2', 'D', 'Z', 'X', '9', 'C', '7', 'P',
        '5', 'I', 'K', '3', 'M', 'J', 'U', 'F', 'R', '4', 'W', 'Y', 'L', 'T', 'N', '6', 'B', 'G', 'Q'};

    /**
     * {@value}补位字符，不能与自定义重复
     */
    private static final char SUFFIX_CHAR = 'A';

    /**
     * 进制长度
     */
    private static final int BIN_LEN = BASE.length;

    /**
     * 生成邀请码最小长度{@value}(默认长度6位)
     */
    private static final int CODE_LEN = 6;

    private static long getCRC32(String objectId) {
        CRC32 crc32 = new CRC32();
        crc32.update(objectId.getBytes());
        return crc32.getValue();
    }

    public static String idToCode(String objectId, Integer length) {
        return idToCode(getCRC32(objectId), length);
    }

    public static String idToDefaultCode(String objectId, Integer length) {
        return idToCode(getCRC32(objectId), CODE_LEN);
    }

    public static String idToDefaultCode(Long id) {
        return idToCode(id, CODE_LEN);
    }

    /**
     * ID转换为邀请码
     *
     * @param id
     * @return
     */
    public static String idToCode(Long id, Integer length) {
        char[] buf = new char[BIN_LEN];
        int charPos = BIN_LEN;

        // 当id除以数组长度结果大于0，则进行取模操作，并以取模的值作为数组的坐标获得对应的字符
        while (id / BIN_LEN > 0) {
            int index = (int) (id % BIN_LEN);
            buf[--charPos] = BASE[index];
            id /= BIN_LEN;
        }

        buf[--charPos] = BASE[(int) (id % BIN_LEN)];
        // 将字符数组转化为字符串
        String result = new String(buf, charPos, BIN_LEN - charPos);

        // 长度不足指定长度则随机补全
        int len = result.length();
        if (len < length) {
            StringBuilder sb = new StringBuilder();
            sb.append(SUFFIX_CHAR);
            Random random = new Random();
            // 去除SUFFIX_CHAR本身占位之后需要补齐的位数
            for (int i = 0; i < length - len - 1; i++) {
                sb.append(BASE[random.nextInt(BIN_LEN)]);
            }

            result += sb.toString();
        }

        return result;
    }

    /**
     * 邀请码解析出ID<br/>
     * 基本操作思路恰好与idToCode反向操作。
     *
     * @param code
     * @return
     */
    public static Long codeToId(String code) {
        char[] charArray = code.toCharArray();
        long result = 0L;
        for (int i = 0; i < charArray.length; i++) {
            int index = 0;
            for (int j = 0; j < BIN_LEN; j++) {
                if (charArray[i] == BASE[j]) {
                    index = j;
                    break;
                }
            }

            if (charArray[i] == SUFFIX_CHAR) {
                break;
            }

            if (i > 0) {
                result = result * BIN_LEN + index;
            } else {
                result = index;
            }
        }

        return result;

    }
}
