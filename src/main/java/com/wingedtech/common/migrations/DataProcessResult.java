package com.wingedtech.common.migrations;

import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;

import java.io.Serializable;

/**
 * 数据处理结果
 */
@Data
public class DataProcessResult implements Serializable {

    public Boolean skipped;

    public Boolean result;

    /**
     * 是否可以继续处理下一个
     */
    public Boolean continueNext;

    /**
     * 当前处理是否是一个成功的处理
     * @return
     */
    public boolean isSuccessful() {
        return BooleanUtils.isTrue(result);
    }

    public boolean isFailed() {
        return !isSuccessful() && !isSkipped();
    }

    public boolean isSkipped() {
        return BooleanUtils.isTrue(skipped);
    }

    public boolean shouldContinue() {
        return BooleanUtils.isNotFalse(continueNext);
    }

    public DataProcessResult abort() {
        continueNext = false;
        return this;
    }

    /**
     * 构建一个成功的DataProcessResult实例
     * @return
     */
    public static DataProcessResult success() {
        DataProcessResult result = new DataProcessResult();
        result.setResult(true);
        return result;
    }

    /**
     * 构建一个失败的DataProcessResult实例
     * @return
     */
    public static DataProcessResult fail() {
        DataProcessResult result = new DataProcessResult();
        result.setResult(false);
        return result;
    }

    public static DataProcessResult skip() {
        DataProcessResult result = new DataProcessResult();
        result.setSkipped(true);
        return result;
    }

    /**
     * 构建一个失败的DataProcessResult实例，并且终止后续处理
     * @return
     */
    public static DataProcessResult failAndAbort() {
        return fail().abort();
    }
}
