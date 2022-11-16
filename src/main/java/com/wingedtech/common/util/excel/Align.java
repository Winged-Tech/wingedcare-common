package com.wingedtech.common.util.excel;

/**
 * 导出字段对齐方式（0：自动；1：靠左；2：居中；3：靠右）
 */
public enum Align {
    /**
     * 自动
     */
    AUTO(0),
    /**
     * 靠左
     */
    LEFT(1),
    /**
     * 居中
     */
    CENTER(2),
    /**
     * 靠右
     */
    RIGHT(3);
    private final int value;

    Align(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
