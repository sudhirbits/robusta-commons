package com.robusta.commons.config;

import com.netflix.config.DynamicPropertyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang.StringUtils.isBlank;


public class SettingsSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsSource.class);

    public String getStringSetting(String name, String defaultValue) {
        return instance.getStringProperty(name, defaultValue).get();
    }

    public int getIntSetting(String name, int defaultValue) {
        return instance.getIntProperty(name, defaultValue).get();
    }

    public long getLongSetting(String name, long defaultValue) {
        return instance.getLongProperty(name, defaultValue).get();
    }

    public boolean getBooleanSetting(String name, boolean defaultValue) {
        return instance.getBooleanProperty(name, defaultValue).get();
    }

    public float getFloatSetting(String name, float defaultValue) {
        return instance.getFloatProperty(name, defaultValue).get();
    }

    public double getDoubleSetting(String name, double defaultValue) {
        return instance.getDoubleProperty(name, defaultValue).get();
    }

    public <T> T getContextualSetting(String name, T defaultValue) {
        return instance.getContextualProperty(name, defaultValue).getValue();
    }
    
    
    private final DynamicPropertyFactory instance;

    public SettingsSource(DynamicPropertyFactory instance) {
        this.instance = instance;
    }

    public String mandatorySetting(String key) {
        String value = this.getStringSetting(key, null);
        if(isBlank(value)) {
            throw new IllegalStateException(String.format("Not expecting NULL or BLANK value: %s for mandatory setting: %s Verify Configuration SettingsSource.", value, key));
        }
        return value;
    }

    public boolean isEnabled(String name, String identifier) {
        final String settingValue = this.getStringSetting(name, null);
        final boolean isEnabled = settingValue == null || commaSeparatedTokensToSet(settingValue).contains(identifier);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("For Setting: {} the Value :{}. Sending isEnabled = {} for id: {}", name, settingValue, isEnabled, identifier);
        }
        LOGGER.debug("Setting name: {} for id: {} isEnabled?: {}", name, identifier, isEnabled);
        return isEnabled;
    }

    private static Set<String> commaSeparatedTokensToSet(String settingValue) {
        return stream(settingValue.split(",")).collect(toSet());
    }
}
