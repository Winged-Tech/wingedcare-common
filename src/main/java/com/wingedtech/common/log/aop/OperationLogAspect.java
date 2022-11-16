package com.wingedtech.common.log.aop;


import com.wingedtech.common.log.service.OperationLoggerService;
import com.wingedtech.common.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;


/**
 * Aspect for logging execution of service and repository Spring components.
 * <p>
 * By default, it only runs with the "dev" profile.
 */
@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    @Autowired
    private OperationLoggerService operationLoggerService;


    /**
     * 用注解的方式定义切入点
     */
    @Pointcut("@annotation(com.wingedtech.common.log.annotation.OperationLogger)")
    public void logPointCut() {
    }

    /**
     * MethodInterceptor
     */
    @Around("logPointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        //请求返回信息
        Object res = null;
        //请求开始时间
        long startTime = System.currentTimeMillis();
        //请求结束时间
        long endTime = 0L;
        try {
            //执行方法
            res = joinPoint.proceed();
            endTime = System.currentTimeMillis();
            return res;
        } finally {
            try {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                String userLogin = Optional.ofNullable(SecurityUtils.getCurrentUserLogin()).map(Optional::get).orElse(null);
                long finalEndTime = endTime;
                Object finalRes = res;
                operationLoggerService.save(userLogin, request, joinPoint, startTime, finalEndTime, finalRes);
            } catch (Exception e) {
                log.error("请求异常：", e);
            }
        }
    }

}
