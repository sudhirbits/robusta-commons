package com.robusta.commons.test.mock;

import com.google.common.base.Predicate;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;

import java.lang.reflect.Field;
import java.util.List;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.addAll;

public class MockFactory {
    public static void initMocks(Object testObject) throws IllegalAccessException {
        List<Field> fields = newArrayList(allNonPrimitiveFieldsFromHierarchy(testObject));
        Field mockeryField = getOnlyElement(filter(fields, new Predicate<Field>() {
            @Override
            public boolean apply(Field input) {
                return input != null && Mockery.class.isAssignableFrom(input.getType());
            }
        }));
        mockeryField.setAccessible(true);
        Mockery mockery = (Mockery) mockeryField.get(testObject);
        if(mockery == null) {
            mockery = new JUnit4Mockery();
            mockeryField.set(testObject, mockery);
        }
        for (Field fieldToBeMocked : filter(fields, new Predicate<Field>() {
            @Override
            public boolean apply(Field input) {
                return input != null && input.isAnnotationPresent(Mock.class);
            }
        })) {
            fieldToBeMocked.setAccessible(true);
            fieldToBeMocked.set(testObject, mockery.mock(fieldToBeMocked.getType()));
        }
    }

    private static List<Field> allNonPrimitiveFieldsFromHierarchy(Object testObject) {
        List<Field> fields = newArrayList();
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
