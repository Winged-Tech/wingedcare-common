package com.wingedtech.common.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.NotNull;

/**
 * 通用VM对象，可配合具体的DTO对象用于example类型的查询接口
 * @param <D>
 */
@Data
public class ExampleQueryVM<D> {

    @NotNull
    @ApiModelProperty(value = "用于查询的example对象，不可为null（支持空对象）", required = true)
    private D example;

    @ApiModelProperty("分页参数，留空即可使用默认分页参数")
    private PageableParam pageable;

    /**
     * 获取当前的Pageable分页参数
     * @return
     */
    @JsonIgnore
    public Pageable getPageableParameter() {
        if (pageable == null) {
            pageable = new PageableParam();
        }
        return pageable.toPageable();
    }

    /**
     * 根据指定的example对象与分页参数构建一个ExampleQueryVM
     * @param example
     * @param pageableParam
     * @param <D>
     * @return
     */
    public static <D> ExampleQueryVM of(D example, PageableParam pageableParam) {
        ExampleQueryVM<D> exampleQueryVM = new ExampleQueryVM<>();
        exampleQueryVM.setExample(example);
        exampleQueryVM.setPageable(pageableParam);
        return exampleQueryVM;
    }

    /**
     * 根据指定的example对象与分页参数构建一个仅查询单个结果的ExampleQueryVM
     * @param example
     * @param <D>
     * @return
     */
    public static <D> ExampleQueryVM forSingleResult(D example) {
        return of(example, PageableParam.of(0, 1));
    }
}
