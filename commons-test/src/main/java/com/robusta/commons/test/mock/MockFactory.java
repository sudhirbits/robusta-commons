package com.robusta.commons.test.mock;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.addAll;
import static java.util.stream.Collectors.toList;

public class MockFactory {
    public static void initMocks(Object testObject) throws IllegalAccessException {
        List<Field> fields = allNonPrimitiveFieldsFromHierarchy(testObject);
        Field mockeryField = findMockeryField(fields);
        final Mockery mockery = getMockery(testObject, mockeryField);
        for (Field fieldToBeMocked : fields.stream().filter(MockFactory::isMockable).collect(toList())) {
            fieldToBeMocked.setAccessible(true);
            fieldToBeMocked.set(testObject, mockery.mock(fieldToBeMocked.getType()));
        }
    }

    private static Field findMockeryField(List<Field> fields) {
        Field mockeryField = fields.stream().filter(MockFactory::isMockery)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Unable to find Mockery"));
        mockeryField.setAccessible(true);
        return mockeryField;
    }

    private static Mockery getMockery(Object testObject, Field mockeryField) throws IllegalAccessException {
        Mockery mockery = (Mockery) mockeryField.get(testObject);
        if(mockery == null) {
            mockery = new JUnit4Mockery();
            mockeryField.set(testObject, mockery);
        }
        return mockery;
    }

    private static boolean isMockable(Field input) {
        return input != null && input.isAnnotationPresent(Mock.class);
    }

    private static boolean isMockery(Field input) {
        return input != null && Mockery.class.isAssignableFrom(input.getType());
    }

    private static List<Field> allNonPrimitiveFieldsFromHierarchy(Object testObject) {
        List<Field> fields = new ArrayList<>();
        return allNonPrimitiveFieldsFromHierarchy(fields, testObject.getClass());
    }

    private static List<Field> allNonPrimitiveFieldsFromHierarchy(List<Field> fields, Class<?> thisClass) {
        addAll(fields, thisClass.getDeclaredFields());
        if(!thisClass.getSuperclass().equals(Object.class)) {
            allNonPrimitiveFieldsFromHierarchy(fields, thisClass.getSuperclass());
        }
        return fields;
    }
}
