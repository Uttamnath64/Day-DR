package com.lit.litnotes.Model;

public class FolderModel {

    private String Id, Name, NumberOfNotes;

    public FolderModel(String id, String name, String numberOfNotes) {
        Id = id;
        Name = name;
        NumberOfNotes = numberOfNotes;
    }

    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getNumberOfNotes() {
        return NumberOfNotes;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setNumberOfNotes(String numberOfNotes) {
        NumberOfNotes = numberOfNotes;
    }
}
