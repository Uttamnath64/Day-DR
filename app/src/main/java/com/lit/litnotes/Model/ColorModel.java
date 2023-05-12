package com.lit.litnotes.Model;

import android.content.res.ColorStateList;

public class ColorModel {
    String Id;
    int Color;
    boolean isChecked;

    public ColorModel(String id, int color, boolean isChecked) {
        Id = id;
        Color = color;
        this.isChecked = isChecked;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
