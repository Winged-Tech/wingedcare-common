package com.wingedtech.common.analytics.service;

import com.wingedtech.common.analytics.service.dto.EventDataDTO;
import com.wingedtech.common.analytics.annotation.EventData;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

/**
 * @author apple
 */
@Service
public class IEventDataService {

    private final EventDataService eventDataService;

    public IEventDataService(EventDataService eventDataService) {
        this.eventDataService = eventDataService;
    }


    public void save(HttpServletRequest request, JoinPoint joinPoint, String userId) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        EventData annotation = signature.getMethod().getAnnotation(EventData.class);
        EventDataDTO eventDataDTO = new EventDataDTO();
        eventDataDTO.setName(annotation.name());
        eventDataDTO.setUserId(userId);
        eventDataDTO.setRequestIp(request.getRemoteAddr());
        eventDataDTO.setRequestUrl(request.getRequestURL().toString());
        eventDataDTO.setCreatedTime(Instant.now());
        eventDataService.save(eventDataDTO);
    }
}
