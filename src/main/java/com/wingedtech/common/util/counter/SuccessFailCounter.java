package com.wingedtech.common.util.counter;

/**
 * 用来统计成功/失败计数的工计数器工具类
 */
public class SuccessFailCounter extends Counters {

    public static final String COUNTER_SUCCESS = "success";
    public static final String COUNTER_FAILURE = "failure";
    public static final String COUNTER_WARNING = "warning";
    public static final String COUNTER_SKIPPED = "skipped";

    public static final String COUNTER_SUMMARY = "total: %d; success: %d; failure: %d; warning: %d; skipped: %d";
    public static final String COUNTER_SUMMARY_WITH_NAME = "%s: " + COUNTER_SUMMARY;

    public long success() {
        return inc(COUNTER_SUCCESS);
    }

    public long failure() {
        return inc(COUNTER_FAILURE);
    }

    public long warning() {
        return inc(COUNTER_WARNING);
    }

    public long skipped() { return inc(COUNTER_SKIPPED); }

    public long getSuccessCount() {
        return getCount(COUNTER_SUCCESS);
    }

    public long getFailureCount() {
        return getCount(COUNTER_FAILURE);
    }

    public long getWarningCount() {
        return getCount(COUNTER_WARNING);
    }

    public long getSkippedCount() { return getCount(COUNTER_SKIPPED); }

    public boolean hasFailure() {
        return getFailureCount() > 0;
    }

    public boolean hasWarning() {
        return getWarningCount() > 0;
    }

    public boolean hasSkipped() { return getSkippedCount() > 0; }

    @Override
    public String toString() {
        return String.format(COUNTER_SUMMARY, getTotal(), getSuccessCount(), getFailureCount(), getWarningCount(), getSkippedCount());
    }

    /**
     * 根据指定的消息获取一个格式化的计数总结,总结内容中带有传入的消息
     * @param message
     * @return
     */
    public String getFormattedResults(String message) {
        return String.format(COUNTER_SUMMARY_WITH_NAME, message, getTotal(), getSuccessCount(), getFailureCount(), getWarningCount(), getSkippedCount());
    }
}
