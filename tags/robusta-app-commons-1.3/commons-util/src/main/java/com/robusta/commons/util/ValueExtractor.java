package com.robusta.commons.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.util.ReflectionUtils.getField;
import static org.springframework.util.ReflectionUtils.invokeMethod;

public interface ValueExtractor<T> {
    Object extract(T instance);
    public static class FromMethod<T> implements ValueExtractor<T> {
        private final Method method;

        FromMethod(Method method) {
            checkArgument(method != null, "Need a not NULL method for initialization");
            this.method = method;
            this.method.setAccessible(true);
        }

        @Override
        public Object extract(T instance) {
            return invokeMethod(method, instance);
        }
    }

    public static class FromField<T> implements ValueExtractor<T> {
        private final Field field;

        public FromField(Field field) {
            checkArgument(field != null, "Need a not NULL field for initialization");
            this.field = field;
            this.field.setAccessible(true);
        }

        @Override
        public Object extract(T instance) {
            return getField(field, instance);
        }
    }
}
