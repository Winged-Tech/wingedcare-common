package com.wingedtech.common.log.service;

import org.aspectj.lang.JoinPoint;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;

public interface OperationLoggerService {

    @Async
    void saveAsync(String userLogin, HttpServletRequest request, JoinPoint joinPoint, long startTime, long endTime, Object res);

    void save(String userLogin, HttpServletRequest request, JoinPoint joinPoint, long startTime, long endTime, Object res);
}
