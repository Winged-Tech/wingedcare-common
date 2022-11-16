package com.wingedtech.common.util.excel;

import com.wingedtech.common.util.excel.annotation.ExcelField;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author taozhou
 * @date 2020/12/3
 */
@Data
@AllArgsConstructor
public class ExcelExportTestData {
    @ExcelField(title="订单ID")
    private String id;

    @ExcelField(title="订单金额", dataFormat = "0.00")
    private Double money;
}
