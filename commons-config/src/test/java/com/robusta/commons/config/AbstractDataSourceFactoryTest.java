package com.robusta.commons.config;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import javax.sql.DataSource;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AbstractDataSourceFactoryTest {
    @Spy private TestDataSourceFactory dataSourceFactory;
    @Mock private DataSource dataSource;

    @Before
    public void setUp() throws Exception {
        dataSourceFactory = new TestDataSourceFactory("jndi/test");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldBuildC3P0DataSourceWhenJndiLookupFails() {
        final DataSource dataSource = dataSourceFactory.fromJndiOrBuildOneIfJndiDoesNotExist();
        assertNotNull(dataSource);
        verify(dataSourceFactory, times(1)).buildLocalDataSource();
    }

    @Test
    public void shouldReturnDataSourceFromJndiLookup() throws Exception {
        SimpleNamingContextBuilder namingContextBuilder = new SimpleNamingContextBuilder();
        namingContextBuilder.bind("jndi/test", dataSource);
        namingContextBuilder.activate();
        final DataSource dataSource = dataSourceFactory.fromJndiOrBuildOneIfJndiDoesNotExist();
        assertThat(dataSource, is(this.dataSource));
        verify(dataSourceFactory, times(0)).buildLocalDataSource();
    }

    @Test
    public void destroy() throws Exception {
        dataSourceFactory.destroy();
    }

    public static class TestDataSourceFactory extends AbstractDataSourceFactory {
        public TestDataSourceFactory(String jndiLookupName) {
            super(jndiLookupName);
        }
    }
}