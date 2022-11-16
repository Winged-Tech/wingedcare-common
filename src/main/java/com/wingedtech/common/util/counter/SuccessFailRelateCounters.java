package com.wingedtech.common.util.counter;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 对于一组Counter中发生错误会造成关联失败的Counter
 *
 * @author zhangyp
 */
public class SuccessFailRelateCounters extends SuccessFailCounters {

    /**
     * 失败关联结构
     * 比如:
     * - key3的失败会导致key2的失败
     * - key2的失败会导致key1的失败
     * 则:
     * - failureRelationMap.put(key3, key2)
     * - failureRelationMap.put(key2, key1)
     */
    private final Map<String, String> failureRelations;

    /**
     * 前置条件结构
     */
    private final Map<String, Boolean> conditionMap;

    public SuccessFailRelateCounters(Map<String, String> failureRelations) {
        this.failureRelations = failureRelations;
        this.conditionMap = new HashMap<>();
    }

    /**
     * 再进行成功，失败，警告，跳过之前必须进行ready
     * ps: ready一定要在与之有关联的嵌套统计之前
     *
     * @param key
     */
    public void ready(String key) {
        conditionMap.put(key, true);
    }


    @Override
    public long failure(String key) {
        failureCondition(key);
        return super.failure(key);
    }

    private void failureCondition(String key) {
        String itemKey = getRelationItem(key);
        if (StringUtils.isNotBlank(itemKey)) {
            this.failureCondition(itemKey);
            conditionMap.put(itemKey, false);
        }
    }

    @Override
    public long success(String key) {
        if (BooleanUtils.isFalse(MapUtils.getBoolean(conditionMap, key))) {
            return super.failure(key);
        } else {
            return super.success(key);
        }
    }

    private String getRelationItem(String key) {
        return failureRelations.get(key);
    }
}
