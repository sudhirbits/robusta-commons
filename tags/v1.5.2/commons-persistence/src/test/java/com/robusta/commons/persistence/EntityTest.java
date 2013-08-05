package com.robusta.commons.persistence;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static com.robusta.commons.test.matchers.ReflectionMatchers.hasPrivateFieldValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class EntityTest {
    private Entity entity;
    private static final String USER_NAME = "username";
    private static final Date DATE = new Date();

    @Before
    public void setUp() throws Exception {
        entity = new Entity();
    }

    @Test
    public void testSetCreatedBy() throws Exception {
        entity.setCreatedBy(USER_NAME);
        assertThatAuditFieldsIsSetWithFieldValue("createdBy", equalTo(USER_NAME));
    }

    private void assertThatAuditFieldsIsSetWithFieldValue(String fieldName, Matcher<?> fieldValueMatcher) {
        assertThat(entity, hasPrivateFieldValue("auditFields", hasPrivateFieldValue(fieldName, fieldValueMatcher)));
    }

    @Test
    public void testSetCreatedDate() throws Exception {
        entity.setCreatedDate(DATE);
        assertThatAuditFieldsIsSetWithFieldValue("createdDate", equalTo(DATE));
    }

    @Test
    public void testSetUpdatedBy() throws Exception {
        entity.setUpdatedBy(USER_NAME);
        assertThatAuditFieldsIsSetWithFieldValue("updatedBy", equalTo(USER_NAME));
    }

    @Test
    public void testSetUpdatedDate() throws Exception {
        entity.setUpdatedDate(DATE);
        assertThatAuditFieldsIsSetWithFieldValue("updatedDate", equalTo(DATE));
    }
}