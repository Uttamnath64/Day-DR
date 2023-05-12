package com.lit.litnotes.Database;

public enum Note {
    ID(0),
    LIST_ID(1),
    TITLE(2),
    COLOR_ID(3),
    DESCRIPTION(4),
    U_DATE_TIME(5),
    DATE_TIME(6);
    private final int value;

    Note(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}
