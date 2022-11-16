package com.wingedtech.common.multitenancy.filter;

import com.wingedtech.common.multitenancy.Tenant;
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
public class MultiTenantPostAuthFilter extends OncePerRequestFilter {

    private final RequestMatcher excludingRequestMatcher;
    private final RequestMatcher ignoringTokenRequestMatcher;

    @Override
    public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc) throws ServletException, IOException {
        if (excludingRequestMatcher != null && excludingRequestMatcher.matches(req)) {
            log.trace("[Multitenancy] Request ignored: {}", req.getRequestURI());
            fc.doFilter(req, res);
            return;
        }
        String tenantId = Tenant.getCurrentTenantId();

        // 没有必要再从token里解析tenantId了

        if (StringUtils.isBlank(tenantId)) {
            log.error("No tenant information for request!");
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unable to identify tenant information.");
            return;
        }

        try {
            fc.doFilter(req, res);
        } finally {
            Tenant.clearCurrentTenant();
        }
    }
}
