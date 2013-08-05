package com.robusta.commons.util;

import com.google.common.collect.Maps;
import com.google.common.reflect.AbstractInvocationHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.reflect.Proxy.newProxyInstance;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.springframework.util.ReflectionUtils.*;

/**
 * Use this util to wrap a concrete class/interface in a java dynamic proxy exposed as a different interface.
 * To be used at the boundary between two architectural layers for exposing in domain or for mapping
 * between two domain objects.
 * <p>Multiple options are available</p>
 * <li>From Fields of the delegate class
 * <pre>{@code
 * public interface Mapped { String value(); }
 * public class Delegate { private String value }
 * }</pre></li>
 * <li>From Methods of the delegate class
 * <pre>{@code
 * public interface Mapped { String value(); }
 * public class Delegate { public String value(); }
 * }</pre></li>
 * <b>Note</b>: Private methods are supported as well.</li>
 * <li>Using a ValueProvider specification
 * <pre>{@code
 * public interface Mapped { String value(); }
 * public class Delegate { public String someOtherMethod(); }
 * mapUsingDelegate(delegate, Delegate.class, Mapped.class, new HashMap<String, ValueExtractor<Delegate>>() {{
 * put("value", new ValueExtractor<Delegate>() {
 *      &#064;Override
 *      public Object extract(Delegate instance) {
 *          return instance.someOtherMethod();
 *      }});
 * }});
 * }</pre></li>
 * <b>Warning</b>: Use caution in using this api to wrap Model objects exposed as Domain since it COULD lead to
 * Hibernate Lazy Initialization exception when accessing lazy objects from the proxy outside of the service object
 * transaction point cut.
 */
@SuppressWarnings("ALL")
public class DelegateMapperUtil {
    public static <Mapped, Delegate> Mapped mapUsingDelegate(final Delegate delegate, Class<Delegate> delegateClass, Class<Mapped> mappedClass) {
        return mapUsingDelegate(delegate, delegateClass, mappedClass, Maps.<String, ValueExtractor<Delegate>>newHashMap());
    }

    public static <Mapped, Delegate> Mapped mapUsingDelegate(final Delegate delegate, final Class<Delegate> delegateClass, final Class<Mapped> mappedClass, final Map<String, ValueExtractor<Delegate>> propertyAlternatives) {
        final Map<String, ValueExtractor> extractorMap = newHashMap();
        checkArgument(propertyAlternatives != null);
        checkArgument(mappedClass != null && mappedClass.isInterface(), "%s is not an interface.", mappedClass);
        Map<String, ValueExtractor> valueExtractorMap = Cache.extractionMetadataFor(delegateClass, mappedClass);
        for (Method method : getAllDeclaredMethods(mappedClass)) {
                final String methodOrFieldNameOfDelegate = method.getName();
                if(!propertyAlternatives.containsKey(methodOrFieldNameOfDelegate)) {
                        checkState(valueExtractorMap.containsKey(methodOrFieldNameOfDelegate), "Unable to find a property accessor or field with name: %s on delegate class: %s", methodOrFieldNameOfDelegate, delegateClass);
                        extractorMap.put(methodOrFieldNameOfDelegate, valueExtractorMap.get(methodOrFieldNameOfDelegate));
                } else {
                    extractorMap.put(methodOrFieldNameOfDelegate, propertyAlternatives.get(methodOrFieldNameOfDelegate));
                }
            }
        return mappedClass.cast(newProxyInstance(mappedClass.getClassLoader(), new Class[]{mappedClass}, new AbstractInvocationHandler() {
            @Override
            public Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
                return extractorMap.get(method.getName()).extract(delegate);
            }
        }));
    }

    private static class Cache {
        private static Map<Key, Map<String, ValueExtractor>> cache = newHashMap();
        private static <Mapped, Delegate> Map<String, ValueExtractor> extractionMetadataFor(Class<Delegate> delegateClass, Class<Mapped> mappedClass) {
            Key key = new Key(delegateClass, mappedClass);
            if(cache.containsKey(key)) {
                return cache.get(key);
            } else {
                Map<String, ValueExtractor> extractorMap = newHashMap();
                for (Method method : getAllDeclaredMethods(mappedClass)) {
                    final String methodOrFieldNameOfDelegate = method.getName();
                        Method accessorMethod = findMethod(delegateClass, methodOrFieldNameOfDelegate);
                        if (accessorMethod == null) {
                            Field field = findField(delegateClass, methodOrFieldNameOfDelegate);
                            if(field != null) {
                                extractorMap.put(methodOrFieldNameOfDelegate, new ValueExtractor.FromField<Delegate>(field));
                            }
                        } else {
                            extractorMap.put(methodOrFieldNameOfDelegate, new ValueExtractor.FromMethod<Delegate>(accessorMethod));
                        }
                }
                cache.put(key, extractorMap);
                return extractorMap;
            }
        }

        private static class Key<Delegate, Mapped> {
            private final Class<Delegate> delegateClass;
            private final Class<Mapped> mappedClass;

            private Key(Class<Delegate> delegateClass, Class<Mapped> mappedClass) {
                this.delegateClass = delegateClass;
                this.mappedClass = mappedClass;
            }

            @Override
            public boolean equals(Object that) {
                return reflectionEquals(this, that);
            }

            @Override
            public int hashCode() {
                return reflectionHashCode(this);
            }
        }
    }
}
