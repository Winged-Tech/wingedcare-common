package com.wingedtech.common.util.counter;

import lombok.Getter;

/**
 * 简易计数器工具类
 */
@Getter
public class Counter {

    private static final long DEFAULT_INITIAL_COUNT = 0L;

    private String name;

    private long count;

    public Counter(String name) {
        this(name, DEFAULT_INITIAL_COUNT);
    }

    public Counter(String name, long initialCount) {
        this.name = name;
        this.count = initialCount;
    }

    public static Counter create(String name) {
        return new Counter(name);
    }

    public long inc() {
        return inc(1);
    }

    public synchronized long inc(int n) {
        return count += n;
    }

    public long dec() {
        return inc(-1);
    }

    public Counter reset() {
        this.count = DEFAULT_INITIAL_COUNT;
        return this;
    }
}
