package com.wingedtech.common.migrations;

import java.util.Collection;

/**
 * 历史数据迁移的数据记录处理器
 * @author taozhou
 * @param <T>
 */
public interface DataProcessor<T> {

    /**
     * 获取处理器的名称代号（英文）
     * @return
     */
    String getName();

    /**
     * 判断当前数据是否需要进行处理
     * @param data
     * @return
     */
    boolean needProcess(T data);

    /**
     * 对数据进行处理，由处理器自定义处理内容（存储数据库等）
     * @param data
     */
    DataProcessResult process(T data);

    /**
     * 获取一页数据用于处理
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Collection<T> getPage(int pageIndex, int pageSize);
}
