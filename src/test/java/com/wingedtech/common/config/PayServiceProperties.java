package com.wingedtech.common.config;

import java.util.Map;

@ConfigPropertiesKey("pay")
public class PayServiceProperties implements ConfigProperties {
    private String[] channels;
    private Map<String, PayChannelProperties> channelProperties;

    public String[] getChannels() {
        return channels;
    }

    public void setChannels(String[] channels) {
        this.channels = channels;
    }

    public Map<String, PayChannelProperties> getChannelProperties() {
        return channelProperties;
    }

    public void setChannelProperties(Map<String, PayChannelProperties> channelProperties) {
        this.channelProperties = channelProperties;
    }
}
