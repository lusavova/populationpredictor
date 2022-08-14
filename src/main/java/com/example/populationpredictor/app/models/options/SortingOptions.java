package com.example.populationpredictor.app.models.options;

public final class SortingOptions {
    private final String field;
    private final Type type;

    public SortingOptions(String field, Type type) {
        this.field = field;
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        ASC, DESC
    }
}