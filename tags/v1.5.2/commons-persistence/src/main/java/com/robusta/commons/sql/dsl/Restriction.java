package com.robusta.commons.sql.dsl;

public class Restriction {
    private final int limit;
    private final int offset;

    public Restriction(int limit, int offset) {
        this.limit = limit;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return String.format("%s,%s", offset, limit);
    }
}
