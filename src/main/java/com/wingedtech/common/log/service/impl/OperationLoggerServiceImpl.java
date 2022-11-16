package com.wingedtech.common.log.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.wingedtech.common.log.annotation.OperationLogger;
import com.wingedtech.common.log.service.OperationLogService;
import com.wingedtech.common.log.service.OperationLoggerService;
import com.wingedtech.common.log.service.dto.OperationLogDTO;
import com.wingedtech.common.util.NetworkUtil;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class OperationLoggerServiceImpl implements OperationLoggerService {

    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper;

    @Override
    public void saveAsync(String userLogin, HttpServletRequest request, JoinPoint joinPoint, long startTime, long endTime, Object res) {
        this.save(userLogin, request, joinPoint, startTime, endTime, res);
    }

    @Override
    public void save(String userLogin, HttpServletRequest request, JoinPoint joinPoint, long startTime, long endTime, Object res) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        OperationLogger annotation = signature.getMethod().getAnnotation(OperationLogger.class);
        OperationLogDTO operationLogDTO = new OperationLogDTO();
        try {
            operationLogDTO.setCreatedBy(userLogin);
            operationLogDTO.setCreatedTime(Instant.now());
            operationLogDTO.setOsName(System.getProperty("os.name"));
            operationLogDTO.setOsVersion(System.getProperty("os.version"));
            operationLogDTO.setUserAgent(request.getHeader("user-agent"));
            operationLogDTO.setRequestIp(NetworkUtil.getIpAddr(request));
            operationLogDTO.setRequestUrl(request.getRequestURL().toString());
            operationLogDTO.setRequestType(request.getMethod());
            operationLogDTO.setRequestParam(operateContent(joinPoint, methodName, request));
            operationLogDTO.setClassPath(signature.getDeclaringTypeName());
            operationLogDTO.setMethodName(methodName);
            operationLogDTO.setStartTime(Instant.ofEpochMilli(startTime));
            operationLogDTO.setEndTime(Instant.ofEpochMilli(endTime));
            operationLogDTO.setDuration(endTime - startTime);
            Object responseData = res;
            if (res instanceof ResponseEntity) {
                operationLogDTO.setStatus(((ResponseEntity) res).getStatusCodeValue());
                responseData = ((ResponseEntity) res).getBody();
            }
            if (annotation != null) {
                operationLogDTO.setDescription(annotation.description());
                if (!annotation.ignoreResponse() && responseData != null) {
                    operationLogDTO.setResponseData(objectMapper.writeValueAsString(responseData));
                }
            }
        } catch (Exception e) {
            log.error("Operation log build error：", e);
        }
        operationLogService.save(operationLogDTO);
    }

    public String operateContent(JoinPoint joinPoint, String methodName, HttpServletRequest request) throws ClassNotFoundException, NotFoundException, IOException {
        Object[] params = joinPoint.getArgs();
        String classType = joinPoint.getTarget().getClass().getName();
        Class<?> clazz = Class.forName(classType);
        String clazzName = clazz.getName();
        Map<String, Object> nameAndArgs = getFieldsName(this.getClass(), clazzName, methodName, params);
        StringBuilder builder = new StringBuilder();
        if (!CollectionUtils.isEmpty(nameAndArgs)) {
            for (Map.Entry<String, Object> stringObjectEntry : nameAndArgs.entrySet()) {
                final Object entryValue = stringObjectEntry.getValue();
                // 序列化时要忽略request以及response对象
                if (entryValue instanceof ServletRequest || entryValue instanceof ServletResponse) {
                    continue;
                }
                String value = objectMapper.writeValueAsString(entryValue);
                builder.append(value);
            }
        }
        if (StringUtils.isEmpty(builder.toString())) {
            builder.append(request.getQueryString());
        }
        return builder.toString();
    }

    private Map<String, Object> getFieldsName(Class cls, String clazzName, String methodName, Object[] args) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        ClassClassPath classPath = new ClassClassPath(cls);
        pool.insertClassPath(classPath);

        CtClass cc = pool.get(clazzName);
        CtMethod cm = cc.getDeclaredMethod(methodName);
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr == null) {
            return ImmutableMap.of();
        }
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        int parameters = cm.getParameterTypes().length;
        Map<String, Object> map = new HashMap<>(parameters);
        for (int i = 0; i < parameters; i++) {
            map.put(attr.variableName(i + pos), args[i]);
        }
        return map;
    }
}
