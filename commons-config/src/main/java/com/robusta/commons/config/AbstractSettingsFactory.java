package com.robusta.commons.config;

import com.netflix.config.*;
import com.netflix.config.sources.JDBCConfigurationSource;
import com.netflix.config.sources.URLConfigurationSource;
import org.apache.commons.configuration.AbstractConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;

public abstract class AbstractSettingsFactory {
    public static final String DATABASE_SETTINGS = "DATABASE_SETTINGS";
    public static final String OVERRIDE_SETTINGS = "OVERRIDE_PROPERTIES";

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final int TEN_MINUTES = 10 * 60 * 1000;
    private static final String OVERRIDE_PROPERTY_FILE_LOCATION = "/opt/config";

    // The following constants would be accessed from tests.
    static final String OVERRIDE_PROPERTIES = "override.properties";
    static final String WP_CONFIG_LOCATION = "WP_CONFIG_LOCATION";
    private ConcurrentCompositeConfiguration finalConfig;

    protected int jdbcRefreshIntervalMillis() {
        return TEN_MINUTES;
    }

    protected String overridePropertiesName() {
        return OVERRIDE_PROPERTIES;
    }

    protected String overridesFolderSystemEnvName() {
        return WP_CONFIG_LOCATION;
    }

    protected String overridesFolderSystemPropertyName() {
        return WP_CONFIG_LOCATION;
    }

    protected String defaultOverrirdesFolderPath() {
        return OVERRIDE_PROPERTY_FILE_LOCATION;
    }

    public abstract String settingsQuery();

    private final DataSource dataSource;

    public AbstractSettingsFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public SettingsSource configurationManager() {

        finalConfig = new ConcurrentCompositeConfiguration();
        DynamicConfiguration overridesConfig = getPropertyFileConfiguration();
        if(overridesConfig != null) {
            LOGGER.debug("[OVERRIDE] Configuration Source: {} found, adding", overridesConfig);
            finalConfig.addConfiguration(overridesConfig, OVERRIDE_SETTINGS);
        }

        LOGGER.info("Adding JDBC Data Source SettingsSource - Dynamic Configuration Source");
        finalConfig.addConfiguration(new DynamicConfiguration(new JDBCConfigurationSource(
                dataSource,
                settingsQuery(),
                "name",
                "value"),
                new FixedDelayPollingScheduler(0, jdbcRefreshIntervalMillis(), TRUE)), DATABASE_SETTINGS);

        ConfigurationManager.install(finalConfig);

        return new SettingsSource(DynamicPropertyFactory.getInstance());
    }

    DynamicConfiguration getPropertyFileConfiguration() {
        File propertyFile = new File(getOverridePropertyFileLocation());

        URL propertiesResource = null;

        if (propertyFile.exists()) {
            LOGGER.debug("[OVERRIDE] Property file from path: {} exists, will use this properties for OVERRIDES.", propertyFile);
            try {
                propertiesResource = propertyFile.toURI().toURL();
            } catch (MalformedURLException e) {
                LOGGER.debug("Ignoring exception in loading property file from path: {}", propertyFile, e);
            }
        } else {
            LOGGER.debug("[OVERRIDE] Property file from path: {} does NOT exist, trying from classpath", propertyFile);
            propertiesResource = AbstractSettingsFactory.class.getClassLoader().getResource(overridePropertiesName());
        }

        DynamicConfiguration source = null;
        if (propertiesResource != null) {
            LOGGER.info("[OVERRIDE] A Valid properties resource: {} was found, building a dynamic polled configuration source",
                    propertiesResource);
            source = new DynamicConfiguration(new URLConfigurationSource(propertiesResource),
                    new FixedDelayPollingScheduler(0, TEN_MINUTES, TRUE));
        }
        return source;
    }

    String getOverridePropertyFileLocation() {
        String configLocation = System.getenv(overridesFolderSystemEnvName());
        if(configLocation == null) {
            configLocation = System.getProperty(overridesFolderSystemPropertyName(), defaultOverrirdesFolderPath());
        }
        if(configLocation != null && !configLocation.trim().equals("")) {
            return configLocation + "/" + overridePropertiesName();
        }
        return defaultOverrirdesFolderPath();
    }

    public void destroy() {
        LOGGER.warn("Stopping Configuration Manager - Dynamic Polling Schedulers");
        if(finalConfig == null)
            return;

        List<AbstractConfiguration> configs = finalConfig.getConfigurations();
        if (configs != null) {
            configs.stream()
                    .filter(config -> config instanceof DynamicConfiguration)
                    .forEach(config -> ((DynamicConfiguration) config).stopLoading());
        }
    }

    public void refresh() {
        refresh(OVERRIDE_SETTINGS, TEN_MINUTES);
        refresh(DATABASE_SETTINGS, jdbcRefreshIntervalMillis());
    }

    private void refresh(String name, int pollInterval) {
        DynamicConfiguration configuration = (DynamicConfiguration) finalConfig.getConfiguration(name);
        PolledConfigurationSource source = configuration.getSource();
        configuration.stopLoading();
        configuration.startPolling(source, new FixedDelayPollingScheduler(0, pollInterval, TRUE));
    }

    public abstract class LocallyCachingPolledConfigurationSource implements PolledConfigurationSource {
        @Override
        public PollResult poll(boolean initial, Object checkPoint) throws Exception {
            Map<String, Object> configurations = loadFromCachedFile();
            if (configurations.isEmpty()) { // cache does not exist, we need to go to server.
                configurations = loadFromCentralServer();
                cacheToFile(configurations);
            }
            return PollResult.createFull(configurations);
        }

        protected abstract void cacheToFile(Map<String, Object> configurations);

        protected abstract Map<String, Object> loadFromCentralServer();

        protected abstract Map<String, Object> loadFromCachedFile();
    }
}
