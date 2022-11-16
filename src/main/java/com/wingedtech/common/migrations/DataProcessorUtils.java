package com.wingedtech.common.migrations;

import com.google.common.collect.Maps;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public class DataProcessorUtils {
    public static Map<String, DataProcessService> listToMap(@NotNull List<DataProcessService> list) {
        Map<String, DataProcessService> map = Maps.newHashMapWithExpectedSize(list.size());
        for (DataProcessService dataProcessService : list) {
            map.put(dataProcessService.getName(), dataProcessService);
        }
        return map;
    }
}
