package com.wingedtech.common.log.service;

import com.wingedtech.common.log.service.dto.OperationLogDTO;

/**
 * @author apple
 */
public interface OperationLogService {

    /**
     * 添加操作日志
     *
     * @param operationLogDTO the operationLogDTO
     */
    void save(OperationLogDTO operationLogDTO);

}
