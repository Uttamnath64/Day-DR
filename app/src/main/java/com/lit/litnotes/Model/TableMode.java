package com.lit.litnotes.Model;


public class TableMode {
    int Id;
    String ShortDay,FullDay,DayDate;
    boolean isSelected,isHaveItem;

    public TableMode(int id, String shortDay, String fullDay, String dayDate, boolean isSelected, boolean isHaveItem) {
        Id = id;
        ShortDay = shortDay;
        FullDay = fullDay;
        DayDate = dayDate;
        this.isSelected = isSelected;
        this.isHaveItem = isHaveItem;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getShortDay() {
        return ShortDay;
    }

    public void setShortDay(String shortDay) {
        ShortDay = shortDay;
    }

    public String getFullDay() {
        return FullDay;
    }

    public void setFullDay(String fullDay) {
        FullDay = fullDay;
    }

    public String getDayDate() {
        return DayDate;
    }

    public void setDayDate(String dayDate) {
        DayDate = dayDate;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isHaveItem() {
        return isHaveItem;
    }

    public void setHaveItem(boolean haveItem) {
        isHaveItem = haveItem;
    }
}
