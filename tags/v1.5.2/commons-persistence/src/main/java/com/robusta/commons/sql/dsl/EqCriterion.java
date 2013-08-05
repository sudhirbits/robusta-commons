package com.robusta.commons.sql.dsl;

public class EqCriterion extends UnaryCriterion {
    private final Object value;

    EqCriterion(Field field, Object value) {
        super(field, Operator.eq, value);
        this.value = value;
    }
}
