package com.robusta.commons.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Application Runtime Environment Abstraction.
 *
 * Environment values: local, test, stage and prod. The environment names are case sensitive.
 * Options to pass the Runtime environment to the application.
 *
 * <li>
 *     Set System Environment Variable - CLOUD_ENV to one of the Environment values.
 *     Set this on profile/ terminal prior to start container.
 *     If using tomcat, use setenv.sh
 * </li>
 * <li>
 *     Set System Pro Variable - CLOUD_ENV to one of the Environment values.
 *     -DCLOUD_ENV={Environment values} e.g. -DCLOUD_ENV=stage.
 *     If using tomcat, use CATALINA_OPTS
 * </li>
 * <li>Default environment value will be local</li>
 */
public enum Environment {
    local, test, stage, prod;

    private static final String CLOUD_ENV = "CLOUD_ENV";
    private static final String DEFAULT_CLOUD_ENV = local.name();

    private static final Logger LOGGER = LoggerFactory.getLogger(Environment.class);

    public static Environment readEnvironment() {
        String environment = System.getenv(CLOUD_ENV);
        if (isBlank(environment)) {
            environment = System.getProperty(CLOUD_ENV, DEFAULT_CLOUD_ENV);
            LOGGER.debug("Cloud environment name obtained from System.properties or default: {}", environment);
        } else {
            LOGGER.debug("Cloud environment name obtained from System.env: {}", environment);
        }
        LOGGER.info("Application Runtime - Cloud environment name: {}", environment);
        return Environment.valueOf(environment);
    }

    private static final String SERVICE_PORT = "SERVICE_PORT";
    private static final String DEFAULT_SERVICE_PORT = "8080";

    public static int servicePort() {
        String servicePort = System.getenv(SERVICE_PORT);
        if(servicePort == null) {
            servicePort = System.getProperty(SERVICE_PORT, DEFAULT_SERVICE_PORT);
        }
        return Integer.parseInt(servicePort);
    }

    public int port() {
        return servicePort();
    }
}
