package com.wingedtech.common.util.counter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.*;

public class SuccessFailCounterTest {

    @Test
    public void test() {
        SuccessFailCounter counter = new SuccessFailCounter();
        counter.success();
        counter.failure();
        counter.warning();
        counter.skipped();
        System.out.println(counter.getFormattedResults("Testing counter"));
        assertThat(counter.getFailureCount()).isEqualTo(1);
        assertThat(counter.getSuccessCount()).isEqualTo(1);
        assertThat(counter.getWarningCount()).isEqualTo(1);
        assertThat(counter.getSkippedCount()).isEqualTo(1);
        assertThat(counter.getTotal()).isEqualTo(4);
    }
}
