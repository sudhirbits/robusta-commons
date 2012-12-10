package com.robusta.commons.util;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DelegateMapperUtilTest {

    private Delegate delegate;

    @Before
    public void setUp() throws Exception {
        delegate = new Delegate(-1, "STRING");
    }

    @Test
    public void testMapUsingDelegate() throws Exception {
        Mapped mapped = DelegateMapperUtil.mapUsingDelegate(delegate, Delegate.class, Mapped.class);
        assertThat(mapped.integer(), is(-1));
        assertThat(mapped.string(), is("STRING"));
    }

    @Test(expected = IllegalStateException.class)
    public void testName() throws Exception {
        DelegateMapperUtil.mapUsingDelegate(delegate, Delegate.class, Mapped2.class);
    }

    @Test
    public void testName_withAlternatives() throws Exception {
        Mapped2 mapped2 = DelegateMapperUtil.mapUsingDelegate(delegate, Delegate.class, Mapped2.class, new HashMap<String, ValueExtractor<Delegate>>() {{
            put("aNewMethod", new ValueExtractor<Delegate>() {
                @Override
                public Object extract(Delegate instance) {
                    return String.valueOf(instance.integer);
                }
            });
        }});
        assertThat(mapped2.aNewMethod(), is("-1"));
        assertThat(mapped2.string(), is("STRING"));
    }


    public interface Mapped2 {
        String string();
        String aNewMethod();
    }

    public class Delegate {
        private Integer integer;
        private String string;

        public Delegate(Integer integer, String string) {
            this.integer = integer;
            this.string = string;
        }

        public String string() {
            return string;
        }
    }

    public interface Mapped {
        Integer integer();
        String string();
    }
}
