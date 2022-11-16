package com.wingedtech.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 常用获取客户端信息的工具
 */
@Slf4j
public class NetworkUtil {

    /**
     * 各种代理
     * <p>
     * X-Forwarded-For：Squid服务代理
     * Proxy-Client-IP：apache服务代理
     * WL-Proxy-Client-IP：weblogic服务代理
     * X-Real-IP：nginx服务代理
     * HTTP_CLIENT_IP：有些代理服务器
     */
    private static final String[] PROXYS = {"x-forwarded-for", "Proxy-Client-IP", "WL-Proxy-Client-IP", "X-Real-IP", "HTTP_CLIENT_IP"};

    /**
     * 获取客户端ip
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            for (String proxy : PROXYS) {
                ipAddress = request.getHeader(proxy);
                if (StringUtils.isNotBlank(ipAddress) && !"unknown".equalsIgnoreCase(ipAddress)) {
                    return ipAddress;
                }
            }
            if (StringUtils.isBlank(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if ("127.0.0.1".equals(ipAddress)) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        log.error("the local host name could not  be resolved into an address", e);
                        return null;
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            // "***.***.***.***".length() = 15
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }
}
