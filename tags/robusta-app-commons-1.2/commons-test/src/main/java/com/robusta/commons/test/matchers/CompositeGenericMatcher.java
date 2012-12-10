package com.robusta.commons.test.matchers;

import org.hamcrest.Matcher;

public class CompositeGenericMatcher<T, M extends CompositeGenericMatcher<T, M>> extends CompositeMatcher<T> {
    private final Class<T> tClass;
    protected final Class<M> matcherClass;

    public CompositeGenericMatcher(Class<T> matchedClass, Class<M> matcherClass) {
        this.tClass = matchedClass;
        this.matcherClass = matcherClass;
    }

    protected M matchProperty(String methodOrFieldName, Matcher propertyMatcher, String descriptor) {
        return matcherClass.cast(super.matchProperty(tClass, methodOrFieldName, propertyMatcher, descriptor));
    }

    protected M thisAsM() {
        return matcherClass.cast(this);
    }
}
