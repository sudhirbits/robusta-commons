package com.robusta.commons.sql.dsl;

import org.junit.Test;

import static com.robusta.commons.sql.dsl.Field.field;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class FieldTest {

    @Test
    public void should_return_true_if_field_has_alias_name() {
        Field field = field("ss");
        assertFalse(field.hasAlias());
        field.as("s");
        assertTrue(field.hasAlias());
    }

    @Test
    public void should_return_lower_of_field_name() {
        assertThat(field("ss").lower().toString(), is(equalTo("LOWER(ss)")));
    }

    @Test
    public void testLikeOneOf() throws Exception {
        assertThat(field("ABC").likeOneOf("One").toString(), is("(ABC LIKE ?)"));
    }

    @Test
    public void testLikeOneOfWithMultiple() throws Exception {
        assertThat(field("ABC").likeOneOf("One", "Two", "Three").toString(), is("((ABC LIKE ?) OR (ABC LIKE ?) OR (ABC LIKE ?))"));
    }

}