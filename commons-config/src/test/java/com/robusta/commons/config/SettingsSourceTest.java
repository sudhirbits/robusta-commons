package com.robusta.commons.config;

import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class SettingsSourceTest {
    private SettingsSource source;
    @Mock private DynamicPropertyFactory instance;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        source = new SettingsSource(instance);
        source = Mockito.spy(source);
    }

    @Test
    public void isEnabled() {
        doReturn(null).when(source).getStringSetting("settingname", null);
        assertTrue(source.isEnabled("settingname", "id"));

        when(source.getStringSetting("settingname", null)).thenReturn("id,id2");
        assertTrue(source.isEnabled("settingname", "id"));

        when(source.getStringSetting("settingname", null)).thenReturn("id2,id3");
        assertFalse(source.isEnabled("settingname", "id"));
    }

    @Test(expected = IllegalStateException.class)
    public void testMandatorySetting() {
        when(instance.getStringProperty("this_setting_does_not_exist_should_throw_exception", null)).thenReturn(new DynamicStringProperty("this_setting_does_not_exist_should_throw_exception", null));
        source.mandatorySetting("this_setting_does_not_exist_should_throw_exception");
    }

    @Test
    public void testMandatorySetting2() {
        when(instance.getStringProperty("settingname", null)).thenReturn(new DynamicStringProperty("this_setting_does_not_exist_should_throw_exception", "value"));
        assertThat(source.mandatorySetting("settingname"), equalTo("value"));
    }
}