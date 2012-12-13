package com.robusta.commons.sql.dsl;

import org.junit.Test;

import static com.robusta.commons.sql.dsl.Field.field;
import static com.robusta.commons.sql.dsl.Table.table;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TableTest {

    @Test
    public void should_get_field_from_table_directly(){
        assertThat(table("table").field("id"), equalTo(field("table.id")));
    }
}
