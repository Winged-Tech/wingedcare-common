package com.wingedtech.common.config;

import com.wingedtech.common.multitenancy.Tenant;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @author ssy
 * @date 2020/7/14 11:27
 */
public class ContextDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        RequestAttributes context = RequestContextHolder.currentRequestAttributes();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        String tenantId = Tenant.isEnabledMultitenancy() && Tenant.isCurrentTenantIdSet() ? Tenant.getCurrentTenantId() : null;
        return () -> {
            try {
                SecurityContextHolder.setContext(securityContext);
                RequestContextHolder.setRequestAttributes(context);
                if (tenantId != null) {
                    Tenant.setCurrentTenantId(tenantId);
                }
                runnable.run();
            } finally {
                RequestContextHolder.resetRequestAttributes();
                SecurityContextHolder.clearContext();
            }
        };
    }
}
