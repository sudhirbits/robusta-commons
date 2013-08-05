package com.robusta.commons.test.matchers;

import com.google.common.collect.Iterables;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Collection;
import java.util.List;

public class UtilityMatchers {
    public static <T> Matcher<List<T>> isAnEmptyList(Class<T> tClass) {
        return new TypeSafeMatcher<List<T>>() {
            @Override
            public boolean matchesSafely(List<T> list) {
                return list != null && list.isEmpty();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is an empty list");
            }
        };
    }

    public static <T> Matcher<List<T>> isOfSize(Class<T> tClass, final int size) {
        return new TypeSafeMatcher<List<T>>() {
            @Override
            public boolean matchesSafely(List<T> list) {
                return list != null && list.size() == size;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is of size").appendValue(size);
            }
        };
    }

    public static <T> Matcher<Iterable<T>> iterableOfSize(Class<T>  tClass, final int size) {
        return new TypeSafeMatcher<Iterable<T>>() {
            @Override
            public boolean matchesSafely(Iterable<T> list) {
                return list != null && Iterables.size(list) == size;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is of size").appendValue(size);
            }
        };
    }

    public static Matcher<? extends Collection> isAnEmptyCollection() {
        return new TypeSafeMatcher<Collection>() {
            @Override
            public boolean matchesSafely(Collection collection) {
                return collection.size() == 0;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is an empty collection");
            }
        };
    }
}
