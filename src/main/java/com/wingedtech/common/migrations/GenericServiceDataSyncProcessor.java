package com.wingedtech.common.migrations;

import com.wingedtech.common.domain.ObjectWithId;
import com.wingedtech.common.service.GenericServiceTemplate;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;

/**
 * 从一个GenericServiceTemplate将数据同步到另一个GenericServiceTemplate的Processor
 * @author taozhou
 */
public class GenericServiceDataSyncProcessor<D extends ObjectWithId> implements DataProcessor<D> {
    private final GenericServiceTemplate<D> dataSource;
    private final GenericServiceTemplate<D> dataTarget;
    private final String processorName;

    public GenericServiceDataSyncProcessor(String name, GenericServiceTemplate<D> dataSource, GenericServiceTemplate<D> dataTarget) {
        this.dataSource = dataSource;
        this.dataTarget = dataTarget;
        this.processorName =  name;
    }

    @Override
    public String getName() {
        return processorName;
    }

    @Override
    public boolean needProcess(D data) {
        return true;
    }

    @Override
    public DataProcessResult process(D data) {
        dataTarget.save(data);
        return DataProcessResult.success();
    }

    @Override
    public Collection<D> getPage(int pageIndex, int pageSize) {
        return dataSource.findAll(PageRequest.of(pageIndex, pageSize)).getContent();
    }
}
