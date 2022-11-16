package com.wingedtech.common.migrations;

import com.wingedtech.common.util.counter.SuccessFailCounter;
import org.springframework.scheduling.annotation.Async;

public interface DataProcessService {
    String getName();
    /**
     * 开始异步数据处理
     * @param tenantId
     */
    @Async
    void startProcessAsync(String tenantId);

    /**
     * 开始数据处理（同步调用）
     * @return
     */
    SuccessFailCounter startProcess();
}
