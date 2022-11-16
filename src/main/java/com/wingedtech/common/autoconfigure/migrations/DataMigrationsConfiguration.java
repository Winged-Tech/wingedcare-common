package com.wingedtech.common.autoconfigure.migrations;

import com.wingedtech.common.constant.ConfigConstants;
import com.wingedtech.common.migrations.Constants;
import com.wingedtech.common.migrations.DataMigrationConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(value = ConfigConstants.ENABLED, prefix = Constants.CONFIG_ROOT)
@Import({DataMigrationConfiguration.class})
public class DataMigrationsConfiguration {
}
