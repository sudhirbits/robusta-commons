package com.robusta.commons.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.PooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.Closeable;
import java.lang.reflect.Proxy;

public abstract class AbstractDataSourceFactory {
    private DataSource localDataSource;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final String jndiLookupName;

    protected AbstractDataSourceFactory(String jndiLookupName) {
        this.jndiLookupName = jndiLookupName;
    }

    public DataSource fromJndiOrBuildOneIfJndiDoesNotExist() {
        JndiDataSourceLookup lookup = new JndiDataSourceLookup();
        try {
            return closeProtected(lookup.getDataSource(jndiLookupName));
        } catch (DataSourceLookupFailureException e) {
            LOGGER.warn("Datasource could not be looked up from JNDI, will build a local Datasource", e);
            return this.localDataSource = buildLocalDataSource();
        }
    }

    protected DataSource closeProtected(final DataSource dataSource) {
        LOGGER.info("Datasource from JNDI will be wrapped in a proxy for CLOSE - WARNING.");
        return (DataSource) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{PooledDataSource.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("close")) {
                        LOGGER.warn("Consumer trying to close the datasource from JNDI. NOT Supported.");
                        return null;
                    } else {
                        return method.invoke(dataSource, args);
                    }
                });
    }

    protected DataSource buildLocalDataSource() {
        LOGGER.info("Building local data source using C3P0 data source connection pool.");
        ComboPooledDataSource localDS = new ComboPooledDataSource();
        try {
            localDS.setDriverClass("com.mysql.jdbc.Driver");
        } catch (PropertyVetoException ignored) {

        }
        localDS.setJdbcUrl(getJdbcUrl());
        localDS.setUser(getUsername());
        localDS.setPassword(getPassword());
        localDS.setMinPoolSize(5);
        localDS.setMaxPoolSize(15);
        localDS.setMaxIdleTime(180);
        localDS.setMaxStatements(10);
        localDS.setIdleConnectionTestPeriod(10);
        localDS.setPreferredTestQuery("Select 1");
        return localDS;
    }

    protected String getPassword() {
        return getDBPassword();
    }

    protected String getUsername() {
        return "root";
    }

    protected String getJdbcUrl() {
        return "jdbc:mysql://localhost:3306/robusta";
    }

    public void destroy() throws Exception {
        if(this.localDataSource != null) {
            Class<? extends DataSource> clazz = this.localDataSource.getClass();
            if(ComboPooledDataSource.class.isAssignableFrom(clazz)) {
                ((ComboPooledDataSource) this.localDataSource).close();
            } else if(Closeable.class.isAssignableFrom(clazz)) {
                ((Closeable) this.localDataSource).close();
            }
        }
    }

    public static final String DB_PASSWORD_PROP = "DB_PASSWORD";

    public static String getDBPassword() {
        String passwordValue = System.getenv(DB_PASSWORD_PROP);
        if(passwordValue == null) {
            passwordValue = System.getProperty(DB_PASSWORD_PROP, "Welcome123!");
        }
        if(passwordValue != null && !passwordValue.trim().equals("")) {
            return passwordValue;
        }
        return "Welcome123!";
    }
}
