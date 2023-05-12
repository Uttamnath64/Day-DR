package com.lit.litnotes.Model;

public class NotesModel {

    private String Id;
    private String Title;
    private String Text;
    private String Time;
    private int ColorId;

    public NotesModel(String id, String title, String text,int colorId, String time) {
        Id = id;
        Title = title;
        Text = text;
        ColorId = colorId;
        Time = time;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setText(String text) {
        Text = text;
    }

    public void setColorId(int colorId) {
        ColorId = colorId;
    }

    public void setTime(String time) {
        Time = time;
    }


    public String getId() {
        return Id;
    }

    public String getTitle() {
        return Title;
    }

    public String getText() {
        return Text;
    }

    public int getColorId() {
        return ColorId;
    }

    public String getTime() {
        return Time;
    }
}
