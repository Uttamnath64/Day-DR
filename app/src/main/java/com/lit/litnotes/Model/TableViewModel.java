package com.lit.litnotes.Model;

public class TableViewModel {
    int id;
    String Text, Date;

    public TableViewModel(int id, String text, String date) {
        this.id = id;
        Text = text;
        Date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
