package com.lit.litnotes.Components;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAndTime {
    SimpleDateFormat format;
    String DateTime;

    public DateAndTime(String DateTime){
        this.DateTime = DateTime;
    }
    @SuppressLint("SimpleDateFormat")
    public String getDateFromTS(){
        try {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date time = format.parse(DateTime);
            assert time != null;
            return new SimpleDateFormat("MMM d, yyyy").format(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    @SuppressLint("SimpleDateFormat")
    public String getTimeFromTS(){
        try {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date time = format.parse(DateTime);
            assert time != null;
            return new SimpleDateFormat("h:mm a").format(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    public String getDateTimeFromTS(){
        return this.getDateFromTS()+" "+this.getTimeFromTS() ;
    }
    @SuppressLint("SimpleDateFormat")
    public String getDateFromDate(){
        try {
            format = new SimpleDateFormat("dd-MM-yyyy");
            Date time = format.parse(DateTime);
            assert time != null;
            return new SimpleDateFormat("MMM d, yyyy").format(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    @SuppressLint("SimpleDateFormat")
    public String getTimeFromTime(){
        try {
            format = new SimpleDateFormat("HH:mm");
            Date time = format.parse(DateTime);
            assert time != null;
            return new SimpleDateFormat("h:mm a").format(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @SuppressLint("SimpleDateFormat")
    public String getDay(boolean Full){
        String Type;
        if(Full) Type = "EEEE";
        else Type = "EEE";
        return new SimpleDateFormat(Type).format(DateTime);
    }

    @SuppressLint("SimpleDateFormat")
    public String getDay(Date date,boolean Full){
        String Type;
        if(Full) Type = "EEEE";
        else Type = "EEE";
        return new SimpleDateFormat(Type).format(date);
    }
}
