package com.lit.litnotes.Model;

public class NoteTabModel {
    private String Id,Name;
    private Boolean Clicked;

    public NoteTabModel(String id, String name, Boolean clicked) {
        Id = id;
        Name = name;
        Clicked = clicked;
    }

    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public Boolean getClicked() {
        return Clicked;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setClicked(Boolean clicked) {
        this.Clicked = clicked;
    }
}
