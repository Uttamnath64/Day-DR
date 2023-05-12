package com.lit.litnotes.Model;

public class TasksModel {
    String Id, Text, ReminderDate, ReminderTime, DateTime;
    boolean isChecked, isReminder,isRepeat;


    public TasksModel(String Id, String Text, String ReminderDate, String ReminderTime, boolean isChecked, String DateTime) {
        this.Id = Id;
        this.Text = Text;
        this.ReminderDate = ReminderDate;
        this.ReminderTime = ReminderTime;
        this.DateTime = DateTime;
        this.isChecked = isChecked;
        this.isRepeat = !ReminderDate.trim().equals("");
        this.isReminder = !ReminderTime.trim().equals("");
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getReminderDate() {
        return ReminderDate;
    }

    public void setReminderDate(String reminderDate) {
        ReminderDate = reminderDate;
    }

    public String getReminderTime() {
        return ReminderTime;
    }

    public void setReminderTime(String reminderTime) {
        ReminderTime = reminderTime;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isReminder() {
        return isReminder;
    }

    public void setReminder(boolean reminder) {
        isReminder = reminder;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }
}