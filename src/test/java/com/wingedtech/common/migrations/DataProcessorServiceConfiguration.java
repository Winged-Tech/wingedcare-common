package com.wingedtech.common.migrations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Configuration
public class DataProcessorServiceConfiguration {

    @Autowired
    private DataMigrationProperties dataMigrationProperties;

    @Bean
    public DataProcessService itemWithLimit() {
        return new DefaultDataProcessService<>(dataMigrationProperties, new DataProcessor<Integer>() {
            @Override
            public String getName() {
                return "item-with-limit";
            }

            @Override
            public boolean needProcess(Integer data) {
                return true;
            }

            @Override
            public DataProcessResult process(Integer data) {
                return DataProcessResult.success();
            }

            @Override
            public Collection<Integer> getPage(int pageIndex, int pageSize) {
                return mockData(pageIndex);
            }
        });
    }

    @Bean
    public DataProcessService itemWithoutLimit() {
        return new DefaultDataProcessService<>(dataMigrationProperties, new DataProcessor<Integer>() {
            @Override
            public String getName() {
                return "item-without-limit";
            }

            @Override
            public boolean needProcess(Integer data) {
                return true;
            }

            @Override
            public DataProcessResult process(Integer data) {
                return DataProcessResult.success();
            }

            @Override
            public Collection<Integer> getPage(int pageIndex, int pageSize) {
                return mockData(pageIndex);
            }
        });
    }

    @Bean
    public DataProcessService parallelItems() {
        return new DefaultDataProcessService<>(dataMigrationProperties, new DataProcessor<Integer>() {

            @Override
            public String getName() {
                return "item-parallel";
            }

            @Override
            public boolean needProcess(Integer data) {
                return true;
            }

            @Override
            public DataProcessResult process(Integer data) {
                return DataProcessResult.success();
            }

            @Override
            public Collection<Integer> getPage(int pageIndex, int pageSize) {
                return mockData(pageIndex);
            }
        });
    }


    private Collection<Integer> mockData(int pageIndex) {
        // mock 返回大于20条数据，用于测试20条数据的处理限制
        if (pageIndex == 0) {
            return Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        } else if (pageIndex == 1) {
            return Lists.newArrayList(11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
        } else if (pageIndex == 2) {
            return Lists.newArrayList(21, 22, 23, 24, 25, 26, 27, 28);
        }
        return ImmutableList.of();
    }
}
