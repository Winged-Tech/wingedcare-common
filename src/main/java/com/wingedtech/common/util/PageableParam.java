package com.wingedtech.common.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

/**
 * 自定义的分页参数类，可使用toPageable参数转换成为Pageable
 */
@Data
public class PageableParam implements Serializable {

    private static final PageableParam UNPAGED_INSTANCE = new PageableParam().setUnpaged();

    @ApiModelProperty(value = "Page number of the requested page", example = "0")
    private int page;

    @ApiModelProperty(value = "Size of a page", example = "20")
    private int size = 20;

    @ApiModelProperty(value = "Sorting criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.")
    private String[] sort;

    private boolean unpaged = false;

    public PageableParam() {
    }

    /**
     * 转换为PageRequest参数
     * @return
     */
    public Pageable toPageable() {
        if (this.unpaged) {
            return Pageable.unpaged();
        }
        List<Order> orders = null;
        if (this.sort != null) {
            orders = new ArrayList();
            String[] var2 = this.sort;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String sorting = var2[var4];
                String property = sorting;
                String direction = null;
                String nullHandling = null;
                if (sorting.indexOf(44) > -1) {
                    String[] parts = sorting.split(",");
                    property = parts[0].trim();
                    direction = parts[1].trim();
                    if (parts.length >= 3) {
                        nullHandling = parts[2].trim();
                    }
                }

                Order order;
                if (direction != null && nullHandling != null) {
                    order = new Order(Direction.fromString(direction), property, Sort.NullHandling.valueOf(nullHandling));
                }
                else if (direction != null) {
                    order = new Order(Direction.fromString(direction), property);
                } else {
                    order = Order.by(property);
                }
                orders.add(order);
            }
        }

        return CollectionUtils.isNotEmpty(orders) ? PageRequest.of(this.page, this.size, Sort.by(orders)) : PageRequest.of(this.page, this.size);
    }

    @JsonIgnore
    public PageableParam setUnpaged() {
        this.unpaged = true;
        return this;
    }

    /**
     * 创建一个不分页的PageableParam实例
     * @return
     */
    public static PageableParam unpaged() {
        return UNPAGED_INSTANCE;
    }

    /**
     * 使用指定的page和size构建一个PageableParam对象
     * @param page
     * @param size
     * @return
     */
    public static PageableParam of(int page, int size) {
        PageableParam pageableParam = new PageableParam();
        pageableParam.setPage(page);
        pageableParam.setSize(size);
        return pageableParam;
    }

    /**
     * 使用指定的page、size以及sort构建一个PageableParam对象
     * @param page
     * @param size
     * @return
     */
    public static PageableParam of(int page, int size, String[] sort) {
        PageableParam pageableParam = new PageableParam();
        pageableParam.setPage(page);
        pageableParam.setSize(size);
        pageableParam.setSort(sort);
        return pageableParam;
    }
}

