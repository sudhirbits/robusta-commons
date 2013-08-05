package com.robusta.commons.util;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.findMethod;

public class ValueExtractorTest {
    public static final String VALUE = "Value";
    private ValueExtractor.FromField<TestObject> fromField;
    private ValueExtractor.FromMethod<TestObject> fromMethod;
    private TestObject testObject;
    @Before
    public void setUp() throws Exception {
        fromField = new ValueExtractor.FromField<TestObject>(findField(TestObject.class, "string"));
        fromMethod = new ValueExtractor.FromMethod<TestObject>(findMethod(TestObject.class, "getString"));
        testObject = new TestObject(VALUE);
    }

    @Test
    public void testExtract() throws Exception {
        assertThat((String) fromField.extract(testObject), is(VALUE));
        assertThat((String) fromMethod.extract(testObject), is(VALUE));
    }

    public static class TestObject {
        private String string;

        public TestObject(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }
    }
}
