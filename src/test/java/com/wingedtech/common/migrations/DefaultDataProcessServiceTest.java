package com.wingedtech.common.migrations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.wingedtech.common.util.counter.SuccessFailCounter;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultDataProcessServiceTest {

    /**
     * 测试正常的处理流程
     */
    @Test
    public void testHappyPath() {
        DefaultDataProcessService<Integer> service = new DefaultDataProcessService(new DataMigrationProperties(), new DataProcessor<Integer>() {
            @Override
            public String getName() {
                return "testHappyPath";
            }

            @Override
            public boolean needProcess(Integer data) {
                if (data == 8 || data == 10) {
                    // 模拟跳过部分数据
                    return false;
                }
                return true;
            }

            @Override
            public DataProcessResult process(Integer data) {
                if (data == 5) {
                    // 模拟失败
                    return DataProcessResult.fail();
                }
                return DataProcessResult.success();
            }

            @Override
            public Collection<Integer> getPage(int pageIndex, int pageSize) {
                if (pageIndex == 0) {
                    return Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
                }
                return ImmutableList.of();
            }
        });
        SuccessFailCounter counter = service.startProcess();
        assertThat(counter.getSuccessCount()).isEqualTo(7);
        assertThat(counter.getFailureCount()).isEqualTo(1L);
        assertThat(counter.getSkippedCount()).isEqualTo(2L);
    }

    /**
     * 测试异常中断的处理流程
     */
    @Test
    public void testAbortOnException() {
        DefaultDataProcessService<Integer> service = new DefaultDataProcessService(new DataMigrationProperties(), new DataProcessor<Integer>() {
            @Override
            public String getName() {
                return "testAbortOnException";
            }

            @Override
            public boolean needProcess(Integer data) {
                if (data == 4) {
                    // 模拟跳过部分数据
                    return false;
                }
                return true;
            }

            @Override
            public DataProcessResult process(Integer data) {
                if (data == 6) {
                    // 模拟异常
                    throw new RuntimeException();
                }
                return DataProcessResult.success();
            }

            @Override
            public Collection<Integer> getPage(int pageIndex, int pageSize) {
                if (pageIndex == 0) {
                    return Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
                }
                return ImmutableList.of();
            }
        });
        SuccessFailCounter counter = service.startProcess();
        assertThat(counter.getSuccessCount()).isEqualTo(4);
        assertThat(counter.getFailureCount()).isEqualTo(1);
        assertThat(counter.getSkippedCount()).isEqualTo(1);
    }
}
