package com.wingedtech.common.filter;

import com.wingedtech.common.filter.content.BusinessSourceContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/api/**")
@ConditionalOnProperty(value = "winged.filter.source", havingValue = "enabled")
public class SourceHeaderFilter extends OncePerRequestFilter {

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String businessSource = request.getHeader("source");
        if (StringUtils.isNotBlank(businessSource)) {
            Assert.notNull(businessSource, "Only non-null SourceRequestContext instances are permitted");
            BusinessSourceContextHolder.setContext(businessSource);
        }
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            BusinessSourceContextHolder.clearContext();
        }
    }
}
