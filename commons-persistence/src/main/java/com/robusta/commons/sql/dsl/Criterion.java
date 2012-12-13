package com.robusta.commons.sql.dsl;

import com.google.common.collect.Iterables;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.newArrayList;
import static com.robusta.commons.sql.dsl.Constants.*;

public abstract class Criterion {
    protected final Operator operator;

    Criterion(Operator operator) {
        this.operator = operator;
    }

    public static Criterion and(final Criterion criterion, final Criterion... criterions) {
        return new Criterion(Operator.and) {

            protected void populate(StringBuilder sb) {
                sb.append(criterion);
                for (Criterion criterion : criterions) {
                    sb.append(SPACE).append(AND).append(SPACE).append(criterion);
                }
            }
        };
    }

    private static Criterion or(final Criterion criterion, final Criterion... criterions) {
        return new Criterion(Operator.or) {

            protected void populate(StringBuilder sb) {
                sb.append(criterion);
                for (Criterion criterion : criterions) {
                    sb.append(SPACE).append(OR).append(SPACE).append(criterion.toString());
                }
            }
        };
    }

    public static Criterion or(final Criterion... criterions) {
        checkArgument(criterions != null);
        return or(newArrayList(criterions));
    }

    public static Criterion or(Iterable<Criterion> criterions) {
        checkArgument(Iterables.size(criterions) >= 2);
        return or(getFirst(criterions, null), toArray(skip(criterions, 1), Criterion.class));
    }


    public static Criterion exists(final Query query) {
        return new Criterion(Operator.exists) {

            protected void populate(StringBuilder sb) {
                sb.append(EXISTS).append(SPACE).append(LEFT_PARENTHESIS).append(query).append(RIGHT_PARENTHESIS);
            }
        };
    }

    public static Criterion not(Criterion criterion) {
        return new Criterion(null) {

            protected void populate(StringBuilder sb) {
                
            }
        };
    }

    protected abstract void populate(StringBuilder sb);

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(LEFT_PARENTHESIS);
        populate(builder);
        builder.append(RIGHT_PARENTHESIS);
        return builder.toString();
    }

}
