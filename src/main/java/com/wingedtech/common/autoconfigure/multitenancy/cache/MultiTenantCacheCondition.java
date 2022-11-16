package com.wingedtech.common.autoconfigure.multitenancy.cache;

import com.wingedtech.common.multitenancy.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;

/**
 * General cache condition used with all cache configuration classes.
 *
 * @author Stephane Nicoll
 * @author Phillip Webb
 * @author Madhura Bhave
 * @since 1.3.0
 */
@Slf4j
class MultiTenantCacheCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context,
                                            AnnotatedTypeMetadata metadata) {


        String sourceClass = "";
        if (metadata instanceof ClassMetadata) {
            sourceClass = ((ClassMetadata) metadata).getClassName();
        }
        ConditionMessage.Builder message = ConditionMessage.forCondition("Cache",
                sourceClass);
        Environment environment = context.getEnvironment();
        try {
            final Binder binder = Binder.get(environment);
            BindResult<Boolean> multiTenancyEnabled = binder.bind(Constants.CONFIG_MULTITENANCY_ENABLED, Boolean.class);
            if (!multiTenancyEnabled.isBound() || BooleanUtils.isNotTrue(multiTenancyEnabled.get())) {
                return ConditionOutcome.noMatch(message.because("Multitenancy is not enabled"));
            }

            BindResult<CacheType> specified = binder
                    .bind("spring.cache.type", CacheType.class);
            if (!specified.isBound()) {
                return ConditionOutcome.match(message.because("automatic cache type"));
            }
            CacheType required = MultiTenantCacheConfigurations
                    .getType(((AnnotationMetadata) metadata).getClassName());
            if (specified.get() == required) {
                return ConditionOutcome
                        .match(message.because(specified.get() + " cache type"));
            }
        }
        catch (BindException ex) {
            log.error("Condition matching error", ex);
        }
        return ConditionOutcome.noMatch(message.because("unknown cache type"));
    }

}
