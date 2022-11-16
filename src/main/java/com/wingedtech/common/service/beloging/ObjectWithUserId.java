package com.wingedtech.common.service.beloging;

/**
 * 定义带有用户标识的对象，用户标识默认是userLogin，也可以是其他标识id，需与ObjectWithUserIdServiceTemplate搭配使用
 */
public interface ObjectWithUserId {
    /**
     * 获取当前对象所属的用户id（通常是userLogin）
     * @return
     */
    String getUserId();

    /**
     * 设置当前对象所属的用户id（通常是userLogin）
     * @return
     */
    void setUserId(String userId);
}
