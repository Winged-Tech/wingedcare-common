package com.wingedtech.common.util.excel;

import com.google.common.collect.Lists;
import com.wingedtech.common.util.excel.annotation.ExcelField;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author taozhou
 * @date 2020/12/3
 */
public class ExcelExportTest {

    @Test
    public void test() throws IOException, InterruptedException {
        try (ExcelExport ee = new ExcelExport("test", "测试导出", ExcelExportTestData.class, ExcelField.Type.EXPORT)) {
            ee.setDataList(Lists.newArrayList(
                new ExcelExportTestData("1", 100.0),
                new ExcelExportTestData("21", (double) 0)
            ));
            File tempFile = File.createTempFile("test", "excel.xlsx");
            tempFile.deleteOnExit();
            FileOutputStream stream = new FileOutputStream(tempFile);
            ee.write(stream);
            System.out.println(tempFile.getAbsolutePath());
            try {
                Desktop.getDesktop().open(tempFile);
            }
            catch(Exception ignored) {}
            Thread.sleep(2000);
        }
    }
}
