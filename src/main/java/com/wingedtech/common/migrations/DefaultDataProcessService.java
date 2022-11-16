package com.wingedtech.common.migrations;

import com.google.common.base.Stopwatch;
import com.wingedtech.common.multitenancy.util.TemporaryTenantContext;
import com.wingedtech.common.time.DateTimeFormatterUtils;
import com.wingedtech.common.time.DateTimeUtils;
import com.wingedtech.common.util.counter.SuccessFailCounter;
import com.wingedtech.common.util.iterators.AbstractInfinitePageIterator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

@Slf4j
public class DefaultDataProcessService<T> implements DataProcessService {

    private final DataMigrationProperties dataMigrationProperties;

    /**
     * 针对当前DataProcessor的配置
     */
    private final DataProcessorProperties processorProperties;

    private final DataProcessor<T> processor;

    private final String logPrefix;

    public DefaultDataProcessService(@NotNull DataMigrationProperties dataMigrationProperties, @NotNull DataProcessor<T> processor) {
        this.dataMigrationProperties = dataMigrationProperties;
        this.processor = processor;
        this.processorProperties = dataMigrationProperties.findProcessorConfig(processor.getName());
        logPrefix = String.format("[%s migrations]", processor.getName());
        log.debug("{} config is: {}", logPrefix, processorProperties);
    }

    @Override
    public String getName() {
        return processor.getName();
    }

    @Override
    @Async
    public void startProcessAsync(@NotBlank String tenantId) {
        try (TemporaryTenantContext ignored = new TemporaryTenantContext(tenantId)) {
            startProcess();
        }
    }

    @Override
    public SuccessFailCounter startProcess() {
        SuccessFailCounter counter = new SuccessFailCounter();
        String batchId = buildBatchId();
        String batchLogHeader = String.format("[%s migrations %s]", processor.getName(), batchId);
        log.info("{} 开始数据迁移处理", batchLogHeader);
        Stopwatch watch = Stopwatch.createStarted();
        Iterator<T> it = buildIterator();
        try {
            if (processorProperties.isParallel()) {
                Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(it, Spliterator.ORDERED);
                StreamSupport.stream(spliterator, true).forEach(item -> {
                    processItem(counter, batchLogHeader, item);
                });
            }
            else {
                for (; it.hasNext(); ) {
                    processItem(counter, batchLogHeader, it.next());
                }
            }
        } catch (DataProcessAbortException abortException) {
            log.info("{} 捕获到DataProcessAbortException, 数据迁移处理已终止", batchLogHeader);
        }
        log.info("{} {} 总耗时: {}ms", batchLogHeader, counter.getFormattedResults("数据迁移结果"), watch.elapsed().toMillis());
        return counter;
    }

    private void processItem(SuccessFailCounter counter, String batchLogHeader, T item) {
        DataProcessResult result = processItem(batchLogHeader, item);
        updateCounterForResult(counter, result);
        if (!result.shouldContinue()) {
            log.warn("{} 因发生未知异常，中断数据迁移处理", batchLogHeader);
            throw new DataProcessAbortException();
        }
        if (result.isFailed() && !processorProperties.isSkipFailures()) {
            log.warn("{} 因数据处理失败，中断数据迁移处理", batchLogHeader);
            throw new DataProcessAbortException();
        }
        if (processorProperties.getLimit() != null && counter.getTotal() >= processorProperties.getLimit()) {
            log.warn("{} 已达到记录处理上限，中断数据迁移处理：{} 条记录", batchLogHeader, processorProperties.getLimit());
            throw new DataProcessAbortException();
        }
    }

    private static void updateCounterForResult(SuccessFailCounter counter, DataProcessResult result) {
        if (result.isSkipped()) {
            counter.skipped();
        }
        else {
            if (result.isSuccessful()) {
                counter.success();
            } else {
                counter.failure();
            }
        }
    }

    protected String buildBatchId() {
        return DateTimeFormatterUtils.formatYMDHMS(DateTimeUtils.nowInstant());
    }

    protected DataProcessorProperties getConfig() {
        return this.processorProperties;
    }

    protected DataProcessResult processItem(String batchLogHeader, T item) {
        if (processor.needProcess(item)) {
            if (processorProperties.isLogEachData()) {
                log.info("{} 开始处理数据： {}", batchLogHeader, item);
            }

            try {
                return processor.process(item);
            } catch (Exception e) {
                log.error("{} 数据处理异常", batchLogHeader, e);
                if (getConfig().isAbortOnException()) {
                    return DataProcessResult.failAndAbort();
                }
                else {
                    return DataProcessResult.fail();
                }
            }
        }
        else {
            if (processorProperties.isLogEachData()) {
                log.info("{} 已跳过数据处理： {}", batchLogHeader, item);
            }
            return DataProcessResult.skip();
        }
    }

    protected Iterator<T> buildIterator() {
        return new AbstractInfinitePageIterator<T>(getConfig().getPageSize()) {
            @Override
            protected Collection<T> retrievePage(int pageIndex, int pageSize) {
                return processor.getPage(pageIndex, pageSize);
            }
        };
    }
}
