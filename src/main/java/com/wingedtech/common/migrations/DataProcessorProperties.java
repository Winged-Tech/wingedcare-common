package com.wingedtech.common.migrations;

import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;

@Data
public class DataProcessorProperties {

    public static final DataProcessorProperties DEFAULT = new DataProcessorProperties();
    /**
     * migration每批次数据 pageSize默认100. 经测试, 当数据量较大时, 如果是一页10条数据, 对速度影响较大(执行时间特别长)
     */
    public static final int DEFAULT_PAGE_SIZE = 100;

    /**
     * 当有部分数据处理失败，是否跳过失败项继续处理。默认true
     */
    private Boolean skipFailures;

    /**
     * 当出现异常时是否中断处理，默认true
     */
    private Boolean abortOnException;

    /**
     * 每页获取多少条数据，默认10条
     */
    private int pageSize = DEFAULT_PAGE_SIZE;

    /**
     * 是否限制执行的记录条数（可用于开发环境）
     */
    private Integer limit;

    /**
     * 是否需要并行执行, 默认是串行 (并行需要DataProcessService支持)
     */
    private Boolean parallel;

    /**
     * 是否需要日志记录每一条执行的数据, 默认true
     */
    private Boolean logEachData;

    public boolean isSkipFailures() {
        return BooleanUtils.isNotFalse(skipFailures);
    }

    public boolean isAbortOnException() {
        return BooleanUtils.isNotFalse(abortOnException);
    }

    public boolean isParallel() {
        return BooleanUtils.isTrue(parallel);
    }

    public boolean isLogEachData() {
        return BooleanUtils.isNotFalse(logEachData);
    }
}
