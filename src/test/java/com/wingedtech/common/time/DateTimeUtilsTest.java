package com.wingedtech.common.time;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class DateTimeUtilsTest {

    @BeforeEach
    public void beforeEach() {
        DateTimeUtils.removeDebugClock();
    }

    @Test
    public void setDebugTimeOffset() {
        DateTimeUtils.setDebugTimeOffset(Duration.ofSeconds(60));
        assertThat(DateTimeUtils.isDebugClockOn()).isTrue();
        DateTimeUtils.removeDebugClock();
        assertThat(DateTimeUtils.isDebugClockOn()).isFalse();
    }

    // 在整个项目build的中，这个测试用例会失败，可能是和执行的速度有关。暂时注释
    @Test
    public void nowInstant() {
        int debugClockOffsetDays = 30;

        Instant instant1 = DateTimeUtils.nowInstant();
        Instant instant2 = Instant.now();
        assertThat(instant1).isNotNull();
        assertThat(Duration.between(instant1, instant2).getSeconds()).isEqualTo(0);

        DateTimeUtils.setDebugTimeOffset(Duration.ofDays(debugClockOffsetDays));
        instant1 = DateTimeUtils.nowInstant();
        instant2 = Instant.now();
        assertThat(Duration.between(instant2, instant1).getSeconds()).isGreaterThanOrEqualTo(Duration.ofDays(debugClockOffsetDays).getSeconds());
    }

    @Test
    public void nowZonedDateTime() {
        int debugClockOffsetDays = 30;

        ZonedDateTime zdt1 = DateTimeUtils.nowZonedDateTime();
        ZonedDateTime zdt2 = ZonedDateTime.now();
        assertThat(zdt1).isNotNull();
        assertThat(Duration.between(zdt1, zdt2).getSeconds()).isEqualTo(0);

        DateTimeUtils.setDebugTimeOffset(Duration.ofDays(debugClockOffsetDays));
        zdt1 = DateTimeUtils.nowZonedDateTime();
        zdt2 = ZonedDateTime.now();
        assertThat(Duration.between(zdt2, zdt1).getSeconds()).isGreaterThanOrEqualTo(Duration.ofDays(debugClockOffsetDays).getSeconds());
    }
}
