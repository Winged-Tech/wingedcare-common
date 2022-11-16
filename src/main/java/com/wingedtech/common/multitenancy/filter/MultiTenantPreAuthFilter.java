package com.wingedtech.common.multitenancy.filter;

import com.wingedtech.common.multitenancy.MultiTenantFilter;
import com.wingedtech.common.multitenancy.Tenant;
import com.wingedtech.common.multitenancy.config.MultiTenancyProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author taozhou
 * @date 2021/6/17
 */
@Slf4j
@AllArgsConstructor
public class MultiTenantPreAuthFilter extends OncePerRequestFilter {

    private final MultiTenancyProperties multiTenancyProperties;
    private final RequestMatcher excludingRequestMatcher;

    @Override
    public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc) throws ServletException, IOException {
        if (excludingRequestMatcher != null && excludingRequestMatcher.matches(req)) {
            log.trace("[Multitenancy] Request ignored: {}", req.getRequestURI());
            Tenant.clearCurrentTenant();
            fc.doFilter(req, res);
            return;
        }
        log.trace("[Multitenancy] Request is {}", req.getRequestURI());

        boolean isUsingDefaultTenantId = false;
        String tenantId = MultiTenantFilter.extractFromDomain(multiTenancyProperties, req);
        if (tenantId == null) {
            tenantId = MultiTenantFilter.extractFromHeader(req);
        }

        if (StringUtils.isBlank(tenantId)) {
            if (multiTenancyProperties.isDefaultTenantSet()) {
                tenantId = multiTenancyProperties.getDefaultTenant();
                // 客户端即没有指定tenantId, 也没有token, 此时是缺省的tenantId
                isUsingDefaultTenantId = true;
                log.trace("Using default tenant: {}", tenantId);
            }
        }

        // 在OAuth2 Authentication处理前设置tenantId, 以这样mongodb token store才能正常读取token
        if (StringUtils.isNotBlank(tenantId)) {
            Tenant.setCurrentTenantId(tenantId, isUsingDefaultTenantId);
        }

        fc.doFilter(req, res);
    }
}
