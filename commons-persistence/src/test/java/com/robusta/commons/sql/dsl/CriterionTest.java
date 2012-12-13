package com.robusta.commons.sql.dsl;

import org.junit.Ignore;
import org.junit.Test;

import static com.robusta.commons.sql.dsl.Criterion.*;
import static com.robusta.commons.sql.dsl.Field.field;
import static com.robusta.commons.sql.dsl.Query.select;
import static com.robusta.commons.sql.dsl.Table.table;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class CriterionTest {

    @Test
    public void should_toString_of_single_criterion() {
        assertThat(field("d").eq("'d'").toString(), equalTo("(d='d')"));
        assertThat(field("d").neq("'d'").toString(), equalTo("(d<>'d')"));
        assertThat(field("d").gt("'d'").toString(), equalTo("(d>'d')"));
        assertThat(field("d").lt("'d'").toString(), equalTo("(d<'d')"));
        assertThat(field("d").isNull().toString(), equalTo("(d IS NULL)"));
        assertThat(field("d").isNotNull().toString(), equalTo("(d IS NOT NULL)"));
        assertThat(field("d").between("'d1'", "'d2'").toString(), equalTo("(d BETWEEN 'd1' AND 'd2')"));
        assertThat(field("d").like("'%d'").toString(), equalTo("(d LIKE '%d')"));
        assertThat(field("d").in("'d1'", "'d2'").toString(), equalTo("(d IN ('d1','d2'))"));
    }

    @Test
    public void should_generate_constraint_by_partial_criterions_and_logic_operator() {
        assertThat(and(field("d").eq("'d'"), field("a").neq("'a'")).toString(), equalTo("((d='d') AND (a<>'a'))"));
        assertThat(or(field("d").eq("'d'"), field("a").neq("'a'")).toString(), equalTo("((d='d') OR (a<>'a'))"));
    }

    @Test
    public void should_create_exists_criterion(){
        assertThat(exists(select().from(table("table"))).toString(), equalTo("(EXISTS (SELECT * FROM table))"));
    }
    
    @Test
    @Ignore
    public void should_reverse_given_criterion_by_not(){
        assertThat(not(field("d").eq("'d'")).toString(), equalTo("d<>'d'"));
    }



}
