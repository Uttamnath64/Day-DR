package com.lit.litnotes.Database;

public enum Task {
    ID(0),
    TEXT(1),
    R_ID(2),
    CHECKED(3),
    U_DATE_TIME(4),
    DATE_TIME(5);
    private final int value;

    Task(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}
