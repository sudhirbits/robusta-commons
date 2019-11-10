package com.robusta.commons.test.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.springframework.util.ReflectionUtils.findMethod;
import static org.springframework.util.ReflectionUtils.invokeMethod;

public class ReflectionMatchers {
    public static <T> TypeSafeMatcher<T> hasPrivateFieldValue(final String fieldName, final Matcher<?> fieldValueMatcher) {
        return new TypeSafeMatcher<T>() {
            @Override
            public boolean matchesSafely(T model) {
                try {
                    Field declaredField = model.getClass().getDeclaredField(fieldName);
                    declaredField.setAccessible(true);
                    return fieldValueMatcher.matches(declaredField.get(model));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(" an object with field of name: ").appendValue(fieldName).appendText(" with value matching: ").appendDescriptionOf(fieldValueMatcher);
            }
        };
    }

    /**
     * Use this to verify equivalence of two objects.
     * The equivalence is measure by matching APIs on both objects that return equal values.
     * @param reference Reference object
     * @param verifiable Object 2
     * @param equivalenceApis List of APIs to be verified.
     * @param <Reference> Reference class. Value returned by API from this class is the expected value.
     * @param <Verifiable> Object 1
     */
    public static <Reference, Verifiable> void assertObjectsEquivalent(Reference reference, Verifiable verifiable, String... equivalenceApis) {
        if (reference == null) {
            assertNull("Object 1 is null, but Object 2 is non null, hence not equivalent", verifiable);
            return;
        } else {
            assertNotNull("Object 1 is non null, but Object 2 is null, hence not equivalent", verifiable);
        }
        Class<?> referenceClass = reference.getClass();
        Class<?> object2Class = verifiable.getClass();
        Stream<String> equivalenceApiStream;
        if (equivalenceApis == null || equivalenceApis.length == 0) {
            equivalenceApiStream = buildListOfEquivalenceApisFromReferenceClass(referenceClass);
        } else {
            equivalenceApiStream = Stream.of(equivalenceApis);
        }
        equivalenceApiStream.forEach(anApi -> {
            Method object1Api = assertExistsAndReturnNoArgsApiWithName(referenceClass, anApi);
            Method object2Api = assertExistsAndReturnNoArgsApiWithName(object2Class, anApi);
            try {
                assertEquals(invokeMethod(object1Api, reference), invokeMethod(object2Api, verifiable));
            } catch (AssertionError mismatched) {
                throw new AssertionError(String.format("Equivalence failed on api: %s() Failure: %s", anApi, mismatched.getMessage()));
            }
        });
    }

    private static Stream<String> buildListOfEquivalenceApisFromReferenceClass(Class<?> referenceClass) {
        return Stream.of(referenceClass.getDeclaredMethods()).map(Method::getName);
    }

    public static Method assertExistsAndReturnNoArgsApiWithName(Class<?> object1Class, String anApi) {
        Method object1Api = findMethod(object1Class, anApi);
        assertNotNull(String.format("Cannot find a public no-args API: %s() in %s", anApi, object1Class.getSimpleName()), object1Api);
        return object1Api;
    }
}
