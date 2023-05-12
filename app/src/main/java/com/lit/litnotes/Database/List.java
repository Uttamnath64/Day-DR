package com.lit.litnotes.Database;

public enum List {
    ID(0),
    TITLE(1),
    U_DATE_TIME(2);
    private final int value;

    List(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}
