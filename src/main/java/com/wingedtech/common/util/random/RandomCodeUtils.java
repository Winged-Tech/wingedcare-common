package com.wingedtech.common.util.random;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * 随机编码工具类
 *
 * @author zhangyp
 */
public class RandomCodeUtils {

    /**
     * 重试次数
     */
    public static final int MAX_RETRY_GENERATE_TIME = 5;

    public static final String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
        "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
        "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
        "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
        "W", "X", "Y", "Z"};

    /**
     * 生成8位随机码
     * 参考自 https://blog.csdn.net/andy_miao858/article/details/9530245
     * 随机重复率无法判定
     *
     * @return
     */
    public static String generateShortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        String code = shortBuffer.toString();
        return code;
    }

    /**
     * 生成8位随机码
     *
     * @param checkIsUniqueCode 判断code在业务场景下唯一
     * @return
     */
    public static String generateShortUuidWithPredicate(Predicate<String> checkIsUniqueCode) {
        int count = 0;

        String code;
        // 不唯一时则需重试生成
        do {
            if (count >= MAX_RETRY_GENERATE_TIME) {
                // 生成编码不唯一重试超过最大重试次数时则直接生成UUID
                code = UUID.randomUUID().toString().replace("-", "");
            } else {
                // 默认
                code = generateShortUuid();
                count++;
            }
        } while (!checkIsUniqueCode.test(code));

        return code;
    }


}
