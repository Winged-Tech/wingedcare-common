package com.wingedtech.common.multitenancy.service.information.properties;

import com.wingedtech.common.multitenancy.Constants;
import com.wingedtech.common.multitenancy.domain.TenantInformation;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ConfigurationProperties(prefix = Constants.CONFIG_MULTITENANCY_ROOT + ".information")
public class TenantInformationProperties {

    @NotNull
    @NotEmpty
    private List<TenantInformation> tenants;
}
