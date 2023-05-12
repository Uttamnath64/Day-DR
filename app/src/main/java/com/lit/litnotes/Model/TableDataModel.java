package com.lit.litnotes.Model;

public class TableDataModel {
    String Text, Time;

    public TableDataModel(String text, String time) {
        Text = text;
        Time = time;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
