package com.robusta.commons.config;

import com.google.common.io.Files;
import com.netflix.config.DynamicConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.when;


public class AbstractSettingsFactoryTest {
    private static final String SETTINGS_QUERY = "select name, value from settings";
    private AbstractSettingsFactory factory;
    @Mock private DataSource dataSource;
    @Mock private Connection connection;
    @Mock private PreparedStatement statement;
    @Mock private ResultSet resultSet;
    private File targetConfigFile;
    private File sourceConfigFile;
    private ArgumentCaptor<String> statementCaptor;

    @Before
    public void setUp() throws Exception {
        assumeTrue(System.getenv(AbstractSettingsFactory.WP_CONFIG_LOCATION) == null);
        MockitoAnnotations.initMocks(this);
        factory = new AbstractSettingsFactory(dataSource) {
            public String settingsQuery() {
                return SETTINGS_QUERY;
            }
        };

        URL sourceConfigDir = AbstractSettingsFactoryTest.class.getResource("/settings-factory-test");
        sourceConfigFile = new File(sourceConfigDir.getFile() + "/" + AbstractSettingsFactory.OVERRIDE_PROPERTIES);
        File sourceDir = new File(sourceConfigDir.getFile());
        String targetDir = sourceDir.getParent();
        targetConfigFile = new File(targetDir + "/" + AbstractSettingsFactory.OVERRIDE_PROPERTIES);
        statementCaptor = ArgumentCaptor.forClass(String.class);
    }

    @After
    public void tearDown() throws Exception {
        System.clearProperty(AbstractSettingsFactory.WP_CONFIG_LOCATION);
		if (targetConfigFile != null && targetConfigFile.exists()) {
			targetConfigFile.delete();
		}

		factory.destroy();
    }

    @Test
    public void testGetPropertyFileConfiguration_unableToLookupFromPathOrClasspath() throws Exception {
        // Assert that it returns NULL which means that there is NO OVERRIDES.
        assertNull(factory.getPropertyFileConfiguration());
    }

    @Test
    public void testGetPropertyFileConfiguration_lookupFromSystemPropertiesWPConfigLocation() throws Exception {
        System.setProperty(AbstractSettingsFactory.WP_CONFIG_LOCATION, settingsFactoryTestPath());
        DynamicConfiguration configuration = factory.getPropertyFileConfiguration();
        assertNotNull(configuration);
    }

    @Test
    public void testGetPropertyFileConfiguration_lookupFromClasspath() throws Exception {
        Files.copy(sourceConfigFile, targetConfigFile);
        assertNotNull(factory.getPropertyFileConfiguration());
    }

    @Test
    public void testGetOverridePropertyFileLocation_shouldPickDefaultLocationOfOptConfig() throws Exception {
    	
        String fileLocation = factory.getOverridePropertyFileLocation();
        assertThat(fileLocation, equalTo("/opt/config/override.properties"));
    }

    @Test
    public void testGetOverridePropertyFileLocation_shouldPickLocationFromSystemProperties() throws Exception {
    	
        System.setProperty(AbstractSettingsFactory.WP_CONFIG_LOCATION, "/tmp/properties");
        String fileLocation = factory.getOverridePropertyFileLocation();
        assertThat(fileLocation, equalTo("/tmp/properties/override.properties"));
    }

    @Test
    public void testConfigurationManager_byDefaultShouldRetrieveFromTheDatabaseSettings() throws Exception {
        System.setProperty(AbstractSettingsFactory.WP_CONFIG_LOCATION, settingsFactoryTestPath());
        expectationsOnDataSource("1234567890987654321");
        SettingsSource source = factory.configurationManager();
        assertNotNull(source);

        assertThat(statementCaptor.getValue(), equalTo(SETTINGS_QUERY));

        assertThat(source.getStringSetting("paytm.client_id", null), equalTo("merchant-robusta-retail"));

        // Override proerties [client_id=test_client_id_other] should override values from database settings[client_id=test_client_id].
        assertThat(source.getStringSetting("client_id", null), equalTo("test_client_id_other"));

        // Properties not in override should come in from database settings.
        assertThat(source.getLongSetting("client_id_does_not_exist_on_database_settings", 0), equalTo(1234567890987654321L));

        assertThat(source.getContextualSetting("run.jobs", "NA"), equalTo("true"));

        expectationsOnDataSource("2234567890987654321");
        factory.refresh();
        assertThat(source.getLongSetting("client_id_does_not_exist_on_database_settings", 0), equalTo(2234567890987654321L));
    }

    private static String settingsFactoryTestPath() {
        return AbstractSettingsFactoryTest.class.getResource("/settings-factory-test").getFile();
    }

    private void expectationsOnDataSource(String value) throws SQLException {
        Mockito.reset(dataSource, connection, statement, resultSet);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(statementCaptor.capture())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, true, false);
        when(resultSet.getObject("name")).thenReturn("client_id",
                "client_id_does_not_exist_on_database_settings", "run.jobs");
        when(resultSet.getObject("value")).thenReturn("test_client_id",value,
                "[{\"if\":{\"@environment\":[\"prod1\"]}, \"value\":\"true\"},{\"if\":{\"@environment\":[\"prod2\", \"prod3\"]},\"value\":\"false\"},{\"value\":\"false\"}]");
    }
}
