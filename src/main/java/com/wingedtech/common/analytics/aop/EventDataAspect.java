package com.wingedtech.common.analytics.aop;


import com.wingedtech.common.analytics.service.IEventDataService;
import com.wingedtech.common.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
public class EventDataAspect {

    @Autowired
    private IEventDataService eventDataService;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;


    /**
     * 用注解的方式定义切入点
     */
    @Pointcut("@annotation(com.wingedtech.common.analytics.annotation.EventData)")
    public void logPointCut() {
    }

    /**
     * MethodInterceptor
     */
    @Around("logPointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            //执行方法
            return joinPoint.proceed();
        } finally {
            try {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                String userLogin = Optional.ofNullable(SecurityUtils.getCurrentUserLogin()).map(Optional::get).orElse(null);
                threadPoolTaskExecutor.submit(() -> eventDataService.save(request, joinPoint, userLogin));
            } catch (Exception e) {
                log.error("请求异常：", e);
            }
        }
    }
}
