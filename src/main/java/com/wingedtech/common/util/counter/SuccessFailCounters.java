package com.wingedtech.common.util.counter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 一个同时容纳多个SuccessFailCounter的工具类,方便同时对多种情况进行计数
 *
 * @author zhangyp
 */
public class SuccessFailCounters {

    public static final String COUNTER_SUMMARY_WITH_NAME = "%s: %s";

    private final Map<String, SuccessFailCounter> counterMap;

    public SuccessFailCounters() {
        this.counterMap = new HashMap<>();
    }

    public long success(String key) {
        SuccessFailCounter counter = getOrSetCounter(key);
        return counter.success();
    }

    private SuccessFailCounter getOrSetCounter(String key) {

        SuccessFailCounter counter = counterMap.get(key);
        if (counter == null) {
            counter = new SuccessFailCounter();
            counterMap.put(key, counter);
        }
        return counter;
    }

    public long failure(String key) {
        SuccessFailCounter counter = getOrSetCounter(key);
        return counter.failure();
    }

    public long warning(String key) {
        SuccessFailCounter counter = getOrSetCounter(key);
        return counter.warning();
    }

    public long skipped(String key) {
        SuccessFailCounter counter = getOrSetCounter(key);
        return counter.skipped();
    }

    public long getSuccessCount(String key) {
        SuccessFailCounter counter = getOrSetCounter(key);
        return counter.getSuccessCount();
    }

    public long getFailureCount(String key) {
        SuccessFailCounter counter = getOrSetCounter(key);
        return counter.getFailureCount();
    }

    public long getWarningCount(String key) {
        SuccessFailCounter counter = getOrSetCounter(key);
        return counter.getWarningCount();
    }

    public long getSkippedCount(String key) {
        SuccessFailCounter counter = getOrSetCounter(key);
        return counter.getSkippedCount();
    }

    public boolean hasFailure(String key) {
        SuccessFailCounter counter = getOrSetCounter(key);
        return counter.hasFailure();
    }

    public boolean hasWarning(String key) {
        SuccessFailCounter counter = getOrSetCounter(key);
        return counter.hasWarning();
    }

    public boolean hasSkipped(String key) {
        SuccessFailCounter counter = getOrSetCounter(key);
        return counter.hasSkipped();
    }

    public long getTotal(String key) {
        SuccessFailCounter counter = getOrSetCounter(key);
        return counter.getTotal();
    }

    public SuccessFailCounter getCounter(String key) {
        SuccessFailCounter counter = counterMap.get(key);
        if (counter == null) {
            counter = new SuccessFailCounter();
        }
        return counter;
    }

    @Override
    public String toString() {
        List<String> results = new ArrayList<>(counterMap.size());
        counterMap.forEach((key, value) -> {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{");
            stringBuilder.append(key);
            stringBuilder.append(" - ");
            stringBuilder.append(value.toString());
            stringBuilder.append("}");
            results.add(stringBuilder.toString());
        });
        return results.stream().collect(Collectors.joining(",", "[", "]"));
    }

    /**
     * 根据指定的消息获取一个格式化的计数总结,总结内容中带有传入的消息
     *
     * @param message
     * @return
     */
    public String getFormattedResults(String message) {
        return String.format(COUNTER_SUMMARY_WITH_NAME, message, toString());
    }
}
