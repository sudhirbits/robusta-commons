package com.robusta.commons.sql.dsl;

import org.junit.Test;

import static com.robusta.commons.sql.dsl.Field.field;

public class GroupByTest {
    @Test
    public void should_generate_groupby_clause() {
        GroupBy groupBy = GroupBy.groupBy(field("field"));
    }
}