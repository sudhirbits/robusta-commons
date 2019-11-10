package com.robusta.commons.test.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.ReflectionUtils.findMethod;
import static org.springframework.util.ReflectionUtils.invokeMethod;

public class CompositeMatcher<T> extends TypeSafeMatcher<T> {
    List<TypeSafeMatcher<T>> matchers = new ArrayList<>();

    protected void add(TypeSafeMatcher<T> aMatcher) {
        matchers.add(aMatcher);
    }

    @Override
    public boolean matchesSafely(T toBeMatched) {
        for (TypeSafeMatcher<T> matcher : matchers) {
            if (!matcher.matches(toBeMatched)) {
                System.out.println(String.format("matcher: '%s' returned false matching: '%s'", matcher, toBeMatched));
                return false;
            }
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(" an instance: ");
        for (TypeSafeMatcher<T> matcher : matchers) {
            matcher.describeTo(description);
            description.appendText(" and ");
        }
    }

    /**
     * Find method with name matching methodName in class tClass and
     * invoke this method on the object being matched to get the property being matched.
     * Send the property value into the property matcher for it to match.
     * Use descriptor to append 'With {descriptor} matching: {property matcher description}'
     * @param tClass Class of the object being matched.
     * @param methodOrFieldName Method name (1st) or field name (2nd) that will return the property being matched.
     * @param propertyMatcher Property value matcher.
     * @param descriptor Embed property name into matcher description.
     * @return CompositeMatcher<T>
     */
    CompositeMatcher<T> matchProperty(final Class<T> tClass, final String methodOrFieldName, final Matcher propertyMatcher, final String descriptor) {
        add(new TypeSafeMatcher<T>() {
            @Override
            public boolean matchesSafely(T t) {
                Method accessorMethod = findMethod(tClass, methodOrFieldName);
                Object propertyValue;
                if (accessorMethod != null) {
                    accessorMethod.setAccessible(true);
                    propertyValue = invokeMethod(accessorMethod, t);
                } else {
                    Field field = ReflectionUtils.findField(tClass, methodOrFieldName);
                    if(field != null) {
                        field.setAccessible(true);
                        propertyValue = ReflectionUtils.getField(field, t);
                    } else {
                        throw new RuntimeException(String.format("Unable to find a property accessor or field with name: %s", methodOrFieldName));
                    }
                }
                boolean matches = propertyMatcher.matches(propertyValue);
                if(!matches) {
                    System.err.println(String.format("Matching failure:: In Class: %s, methodOrFieldName: %s, property value: %s, property matcher: %s", tClass, methodOrFieldName, propertyValue, propertyMatcher));
                }
                return matches;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(String.format("with %s matching: ", descriptor)).appendDescriptionOf(propertyMatcher);
            }
        });
        return this;
    }
}
