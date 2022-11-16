package com.wingedtech.common.message.bpm;

import lombok.Data;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author sunbjx
 * @since 2019-01-22 18:30
 */
@Data
public class BpmMessageEvent implements Serializable {

    private static final long serialVersionUID = 5632469288896513872L;

    /**
     * Message名称
     */
    private String messageName;
    /**
     * 是否一个流程启动事件
     */
    private boolean isStartEvent;
    /**
     * 流程定义Key + 业务对象唯一Id，如果该业务对象关联的ProcessInstance不存在，则会启动一个新的ProcessInstance参数
     */
    private String businessKey;

    /**
     * ProcessInstance执行所用的参数
     */
    private Map<String, Object> variables;

    public BpmMessageEvent withVariable(String variableName, Object variable) {
        if (variables == null) {
            variables = new LinkedHashMap<>();
        }
        MapUtils.safeAddToMap(variables, variableName, variable);
        return this;
    }
}
