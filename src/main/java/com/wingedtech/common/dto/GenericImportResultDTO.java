package com.wingedtech.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 通用同步导入数据结果数据对象
 *
 * @author apple
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericImportResultDTO<T> implements Serializable {

    private static final long serialVersionUID = -8142582588480795611L;

    @JsonIgnore
    private AtomicInteger failCount = new AtomicInteger();

    /**
     * 总数
     */
    private int total;

    /**
     * 成功数量
     */
    private int successNumber;

    /**
     * 失败数量
     */
    private int failNumber;

    /**
     * 错误数据文件下载地址
     */
    private String errorViewUrl;

    /**
     * 错误数据展示
     */
    private List<ErrorView<T>> errorView;


    @Data
    public static class ErrorView<T> {

        /**
         * 错误数据
         */
        private T data;
        /**
         * 错误详情
         */
        private String details;

        public ErrorView(T data, String details) {
            this.data = data;
            this.details = details;
        }
    }

    public GenericImportResultDTO(int total) {
        this.total = total;
    }

    public int getSuccessNumber() {
        return total - failNumber;
    }

    public void addError(T data, String details) {
        List<ErrorView<T>> errorView = this.getErrorView();
        if (CollectionUtils.isEmpty(errorView)) {
            this.errorView = Lists.newArrayList(new ErrorView<>(data, details));
        } else {
            errorView.add(new ErrorView<>(data, details));
        }
        this.failNumber = failCount.incrementAndGet();
    }
}
