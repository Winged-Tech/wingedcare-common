package com.wingedtech.common.analytics.service;

import com.wingedtech.common.analytics.service.dto.EventDataDTO;

/**
 * @author apple
 */
public interface EventDataService {

    /**
     * 保存
     *
     * @param eventDataDTO 源数据
     */
    void save(EventDataDTO eventDataDTO);
}
