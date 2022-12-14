package com.wingedtech.common.multitenancy;

import com.wingedtech.common.multitenancy.config.MultiTenancyProperties;
import com.wingedtech.common.multitenancy.domain.TenantInformation;
import com.wingedtech.common.multitenancy.oauth2.MultiTenantOAuth2Helper;
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
 * A filter that gets the tenant id from request header.
 *
 * @author taozhou
 */
@Slf4j
public class MultiTenantFilter extends OncePerRequestFilter {
    private final MultiTenancyProperties multiTenancyProperties;
    private final RequestMatcher excludingRequestMatcher;
    private final RequestMatcher ignoringTokenRequestMatcher;

    public MultiTenantFilter(MultiTenancyProperties multiTenancyProperties, RequestMatcher excludingRequestMatcher, RequestMatcher ignoringTokenRequestMatcher) {
        this.multiTenancyProperties = multiTenancyProperties;
        this.excludingRequestMatcher = excludingRequestMatcher;
        this.ignoringTokenRequestMatcher = ignoringTokenRequestMatcher;
    }

    @Override
    public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc) throws ServletException, IOException {
        if (excludingRequestMatcher != null && excludingRequestMatcher.matches(req)) {
            log.trace("[Multitenancy] Request ignored: {}", req.getRequestURI());
            fc.doFilter(req, res);
            return;
        }
        log.trace("[Multitenancy] Request is {}", req.getRequestURI());

        boolean isUsingDefaultTenantId = false;
        String tenantId = extractFromDomain(multiTenancyProperties, req);
        if (tenantId == null) {
            tenantId = extractFromHeader(req);
        }

        String tenantIdFromToken = null;
        if (ignoringTokenRequestMatcher != null && ignoringTokenRequestMatcher.matches(req)) {
            log.trace("[Multitenancy] Skipped token extraction for request.");
        } else {
            tenantIdFromToken = extractFromToken();
        }

        // ??????header???????????????tenant?????????token?????????tenant?????????tenant id????????????
        if (StringUtils.isNotBlank(tenantIdFromToken)) {
            if (StringUtils.isNotBlank(tenantId) && !StringUtils.equals(tenantIdFromToken, tenantId)) {
                log.warn("Tenant id from header {} mismatches with tenant id from token {}", tenantId, tenantIdFromToken);
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not authorized to access tenant.");
                return;
            }
            tenantId = tenantIdFromToken;
        }

        if (StringUtils.isBlank(tenantId)) {
            if (multiTenancyProperties.isDefaultTenantSet()) {
                tenantId = multiTenancyProperties.getDefaultTenant();
                // ????????????????????????tenantId, ?????????token, ??????????????????tenantId
                isUsingDefaultTenantId = true;
                log.trace("Using default tenant: {}", tenantId);
            } else {
                log.error("No tenant information for request!");
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unable to identify tenant information.");
                return;
            }
        }

        Tenant.setCurrentTenantId(tenantId, isUsingDefaultTenantId);

        try {
            fc.doFilter(req, res);
        } finally {
            Tenant.clearCurrentTenant();
        }
    }

    public static String extractFromHeader(HttpServletRequest req) {
        String tenantId = req.getHeader(Constants.HEADER_TENANT_ID);
        if (StringUtils.isNotEmpty(tenantId)) {
            // do nothing.
            log.trace("Extracted tenant id from header: {}", tenantId);
        }
        return tenantId;
    }

    public static String extractFromDomain(MultiTenancyProperties multiTenancyProperties, HttpServletRequest req) {
        String tenantId = null;
        if (multiTenancyProperties.isEnableDomainMatching()) {
            String domain = req.getServerName().toLowerCase();
            TenantInformation tenantInformation = Tenant.getTenantInformationService().getTenantByDomain(domain);

            // ???????????????????????????????????????????????????????????????????????????????????????ENABLE_SUB_DOMAIN_FILTER????????????false???
            if (tenantInformation == null && MultiTenancyProperties.ENABLE_SUB_DOMAIN_FILTER) {
                final int index = domain.indexOf('.');
                if (index > -1) {
                    domain = domain.substring(0, index);
                }
                tenantInformation = Tenant.getTenantInformationService().getTenantBySubDomain(domain);
            }

            if (tenantInformation != null) {
                tenantId = tenantInformation.getId();
                log.trace("Extracted tenant id from domain: {}", tenantId);
            }
        }
        return tenantId;
    }

    public static String extractFromToken() {
        final String currentTenantId = MultiTenantOAuth2Helper.getCurrentTenantId();
        if (currentTenantId != null) {
            log.trace("[Multitenancy] Extracted tenant id from token: {}", currentTenantId);
        }
        return currentTenantId;
    }
}
