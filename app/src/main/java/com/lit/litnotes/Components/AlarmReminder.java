package com.lit.litnotes.Components;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Icon;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.lit.litnotes.Activity.HomeScreen;
import com.lit.litnotes.Database.Notify;
import com.lit.litnotes.Database.SQLiteDBHelper;
import com.lit.litnotes.Database.SQLiteDBManager;
import com.lit.litnotes.Database.Table;
import com.lit.litnotes.Database.Task;
import com.lit.litnotes.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AlarmReminder extends BroadcastReceiver {

    SQLiteDBHelper sqLiteDBHelper;
    SQLiteDBManager sqLiteDBManager;

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            ReminderHelper reminderHelper = new ReminderHelper(context);
            long id = intent.getLongExtra("id", 0);
            String title = intent.getStringExtra("title");
            int type = intent.getIntExtra("type",0);
            String body = intent.getStringExtra("body");
            sqLiteDBHelper = new SQLiteDBHelper(context);
            sqLiteDBManager = new SQLiteDBManager(context);
            sqLiteDBManager.open();

            // set notification
            if(type == 1){
                try {
                    Cursor row = sqLiteDBManager.fetch("Select * from "+ SQLiteDBHelper.TR_TABLE+" Where R_Id = '"+id+"' ");
                    row.moveToNext();
                    Cursor notify = sqLiteDBManager.fetch("Select * from "+SQLiteDBHelper.TR_NOTIFY+" Where Id = '"+id+"' ");
                    notify.moveToNext();
                    Date dDate;

                    Calendar dateTime = Calendar.getInstance();
                    Calendar calendar = new GregorianCalendar();
                    calendar.add(Calendar.DAY_OF_MONTH,7);

                    dDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse((new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime()) + " " + notify.getString(Notify.R_TIME.getValue())));
                    assert dDate != null;
                    dateTime.setTime(dDate);
                    dateTime.set(Calendar.SECOND, 0);
                    dateTime.set(Calendar.MILLISECOND, 0);
                    new ReminderHelper(context).scheduleNotification(id, 1, "TimeTable Reminder", row.getString(Table.TITLE.getValue()), dateTime.getTimeInMillis());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    Cursor row = sqLiteDBManager.fetch("Select * from "+ SQLiteDBHelper.TR_TASK+" Where R_Id = '"+id+"' ");
                    row.moveToNext();
                    Cursor notify = sqLiteDBManager.fetch("Select * from "+SQLiteDBHelper.TR_NOTIFY+" Where Id = '"+id+"' ");
                    notify.moveToNext();
                    if(!notify.getString(Notify.R_TIME.getValue()).trim().equals("")) {
                        Date dDate;
                        Calendar dateTime = Calendar.getInstance();
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_MONTH,1);
                        dDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime())+ " " + notify.getString(Notify.R_TIME.getValue()));
                        assert dDate != null;
                        dateTime.setTime(dDate);
                        dateTime.set(Calendar.SECOND, 0);
                        dateTime.set(Calendar.MILLISECOND, 0);
                        new ReminderHelper(context).scheduleNotification(id, 0, "Task Reminder", row.getString(Task.TEXT.getValue()), dateTime.getTimeInMillis());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if(type == 0){
                body = "Task Reminder | "+body;
            }else{
                body = "TImeTable Reminder | "+body;
            }
            Intent notificationIntent = new Intent(context, HomeScreen.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notificationIntent.putExtra("id",id);

            Intent actionIntent = new Intent(context,NotificationActionBtn.class);
            actionIntent.setAction("lit");
            actionIntent.putExtra("id",id);

            PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context,0,actionIntent,PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,notificationIntent,PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            @SuppressLint("RestrictedApi") NotificationCompat.Action action = new NotificationCompat.Action.Builder(IconCompat.createFromIcon(Icon.createWithResource(context, R.drawable.checkbox_checked)), "Done",actionPendingIntent).build();

            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);

            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Ringtone ringtone = RingtoneManager.getRingtone(context,alarmUri);
            ringtone.play();

            reminderHelper.notify((int) id,title,body,pendingIntent,action,type);
        }
    }
}
