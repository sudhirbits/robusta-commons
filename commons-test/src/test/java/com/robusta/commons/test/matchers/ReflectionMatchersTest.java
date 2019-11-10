package com.robusta.commons.test.matchers;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ReflectionMatchersTest {
    private TestObject object;
    private TestObject2 object2;

    @Before
    public void setUp() throws Exception {
        object = new TestObject();
        object2 = new TestObject2();
    }

    @Test
    public void hasPrivateFieldValue() {
        assertThat(object, ReflectionMatchers.hasPrivateFieldValue("field1", equalTo("FIELD1")));
        assertThat(object, ReflectionMatchers.hasPrivateFieldValue("field2", notNullValue(Object.class)));
    }

    @Test
    public void assertObjectsEquivalent() {
        ReflectionMatchers.assertObjectsEquivalent(object, object2);
    }

    @Test(expected = AssertionError.class)
    public void assertExistsAndReturnNoArgsApiWithName() {
        ReflectionMatchers.assertExistsAndReturnNoArgsApiWithName(TestObject.class, "apiDoesNotExist");
    }

    @Test
    public void assertExistsAndReturnNoArgsApiWithName_2() {
        assertNotNull(ReflectionMatchers.assertExistsAndReturnNoArgsApiWithName(TestObject.class, "getField"));
    }

    public static class TestObject {
        private String field1 = "FIELD1";
        private Object field2 = new Object();

        public String getField() {
            return field1;
        }
    }

    public static class TestObject2 {
        private String field1 = "FIELD1";
        private Object field2 = new Object();

        public String getField() {
            return field1;
        }
    }
}