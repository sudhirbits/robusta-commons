package com.robusta.commons.sql.dsl;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

import static com.robusta.commons.sql.dsl.Constants.*;
import static com.robusta.commons.sql.dsl.Table.table;
import static java.util.Arrays.asList;

public class Query {

    private Table table;
    private Criterion criterion = null;
    private List<Field> fields = new ArrayList<Field>();
    private List<Join> joins = new ArrayList<Join>();
    private List<Field> groupBies = new ArrayList<Field>();
    private List<Order> orders = new ArrayList<Order>();
    private List<Criterion> havings = new ArrayList<Criterion>();
    private Restriction restriction;

    private Query(Field... fields) {
        this.fields.addAll(asList(fields));
    }

    private Query(List<Field> fields) {
        this.fields = fields;
    }

    public Query(Table table,List<Field> fields){
        this.table = table;
        this.fields = fields;
    }


    public Query and(Criterion criterion) {
        if(criterion != null) {
            where(Criterion.and(this.criterion, criterion));
        }
        return this;
    }

    public static Query select(Field... fields) {
        return new Query(fields);
    }

    public static Query select(List<Field> fields) {
        return new Query(fields);
    }

    public Query from(Table table) {
        this.table = table;
        return this;
    }

    public Query join(Join... join) {
        joins.addAll(asList(join));
        return this;
    }

    public Query where(Criterion criterion) {
        this.criterion = criterion;
        return this;
    }

    public Query groupBy(Field... groupBy) {
        groupBies.addAll(asList(groupBy));
        return this;
    }

    public Query orderBy(Order... order) {
        orders.addAll(asList(order));
        return this;
    }

    public Query appendSelectFields(Field... fields) {
        this.fields.addAll(asList(fields));
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass()) && this.toString().equals(o.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sql = new StringBuilder();
        visitSelectClause(sql);
        visitFromClause(sql);
        visitJoinClause(sql);
        visitWhereClause(sql);
        visitGroupByClause(sql);
        visitOrderByClause(sql);
        visitRestrictionClause(sql);
        return sql.toString().trim();
    }

    public String insertStatement() {
        StringBuilder sql = new StringBuilder();
        visitInsertClause(sql);
        visitValuesClause(sql);
        return sql.toString().trim();
    }

    private void visitRestrictionClause(StringBuilder sql) {
        if (restriction == null) {
            return;
        }
        sql.append(LIMIT);
        sql.append(SPACE).append(restriction).append(SPACE);
    }

    private void visitOrderByClause(StringBuilder sql) {
        if (orders.isEmpty()) {
            return;
        }
        sql.append(ORDER_BY);
        for (Order order : orders) {
            sql.append(SPACE).append(order).append(COMMA);
        }
        sql.deleteCharAt(sql.length() - 1).append(SPACE);
    }

    private void visitGroupByClause(StringBuilder sql) {
        if (groupBies.isEmpty()) {
            return;
        }
        sql.append(GROUP_BY);
        for (Field groupBy : groupBies) {
            sql.append(SPACE).append(groupBy).append(COMMA);
        }
        sql.deleteCharAt(sql.length() - 1).append(SPACE);
        if (havings.isEmpty()) {
            return;
        }
        sql.append("HAVING");
        for (Criterion havingCriterion : havings) {
            sql.append(SPACE).append(havingCriterion).append(COMMA);
        }
        sql.deleteCharAt(sql.length() - 1).append(SPACE);
    }

    private void visitWhereClause(StringBuilder sql) {
        if (criterion == null) {
            return;
        }
        sql.append(WHERE).append(SPACE).append(criterion).append(SPACE);
    }

    private void visitJoinClause(StringBuilder sql) {
        for (Join join : joins) {
            sql.append(join).append(SPACE);
        }
    }

    private void visitFromClause(StringBuilder sql) {
        if (table == null) {
            return;
        }
        sql.append(FROM).append(SPACE).append(table).append(SPACE);
    }

    private void visitSelectClause(StringBuilder sql) {
        sql.append(SELECT).append(SPACE);
        if (fields.isEmpty()) {
            sql.append(ALL).append(SPACE);
            return;
        }
        for (Field field : fields) {
            sql.append(field).append(COMMA);
        }
        sql.deleteCharAt(sql.length() - 1).append(SPACE);
    }

    private void visitInsertClause(StringBuilder sql) {
        sql.append(INSERT).append(SPACE).append(table).append(SPACE).append(LEFT_PARENTHESIS);
        sql.append(Joiner.on(COMMA).join(fields)).append(RIGHT_PARENTHESIS).append(SPACE);
    }

    private void visitValuesClause(StringBuilder sql) {
        sql.append(VALUES).append(SPACE).append(LEFT_PARENTHESIS);
        for (Field field : fields) {
            sql.append(PLACEHOLDER).append(COMMA);
        }
        sql.deleteCharAt(sql.length() - 1).append(RIGHT_PARENTHESIS);
    }

    public Table as(String alias) {
        return table(LEFT_PARENTHESIS + this.toString() + RIGHT_PARENTHESIS).as(alias);
    }

    public Query having(Criterion criterion) {
        this.havings.add(criterion);
        return this;
    }

    public Query restrict(int limit, int offset) {
        this.restriction = new Restriction(limit, offset);
        return this;
    }
}
