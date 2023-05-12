package com.lit.litnotes.Database;

public enum Table {
    ID(0),
    TITLE(1),
    DAY_ID(2),
    R_ID(3),
    U_DATE_TIME(4),
    DATE_TIME(5);
    private final int value;

    Table(int i) {
        this.value = i;
    }

    public int getValue() {
            return value;
        }

}
