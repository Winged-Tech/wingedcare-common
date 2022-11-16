package com.wingedtech.common.config;

import com.wingedtech.common.constant.ConfigConstants;

public class Constants {

    /**
     * ConfigService的所有配置项根路径
     */
    public static final String CONFIG_SERVICE_PROPERTIES_ROOT = ConfigConstants.WINGED_CONFIG_ROOT + "config";

    /**
     * 指定配置服务provider的配置项
     */
    public static final String CONFIG_PROVIDER_KEY = CONFIG_SERVICE_PROPERTIES_ROOT + ".provider";

    /**
     * 所有provider配置项的配置根路径
     */
    public static final String CONFIG_PROVIDERS_ROOT = CONFIG_SERVICE_PROPERTIES_ROOT + ".providers";
}
