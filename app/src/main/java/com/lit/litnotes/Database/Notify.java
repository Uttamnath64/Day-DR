package com.lit.litnotes.Database;

public enum Notify {
    ID(0),
    TYPE(1),
    R_DATE(2),
    R_TIME(3);
    private final int value;

    Notify(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}
