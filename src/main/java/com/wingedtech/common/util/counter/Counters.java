package com.wingedtech.common.util.counter;

import java.util.HashMap;
import java.util.Map;

/**
 * 一个同时容纳多个Counter的工具类,方便同时对多种情况进行计数
 */
public class Counters {

    private final Map<String, Counter> counters;

    public Counters() {
        this.counters = new HashMap<>();
    }

    public long inc(String name) {
        return getCounter(name).inc();
    }

    public long inc(String name, int n) {
        return getCounter(name).inc(n);
    }

    public long dec(String name) {
        return getCounter(name).dec();
    }

    public Counter reset(String name) {
        return getCounter(name).reset();
    }

    public long getCount(String name) {
        return getCounter(name).getCount();
    }

    public long getTotal() {
        long total  = 0;
        for (Counter value : counters.values()) {
            total += value.getCount();
        }
        return total;
    }

    private synchronized Counter getCounter(String name) {
        Counter counter = this.counters.get(name);
        if (counter == null) {
            counter = Counter.create(name);
            this.counters.put(counter.getName(), counter);
        }
        return counter;
    }
}
