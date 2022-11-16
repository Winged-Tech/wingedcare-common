package com.wingedtech.common.service.beloging;

import org.apache.commons.lang3.StringUtils;

public class ObjectWithUserIdUtils {

    /**
     * 判断指定的对象是否属于指定的userLogin
     * @param object
     * @param userId 用户标识
     * @return
     */
    public static boolean isForUser(ObjectWithUserId object, String userId) {
        return object.getUserId() != null && StringUtils.equals(object.getUserId(), userId);
    }
}
