package com.lit.litnotes.Components;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.lit.litnotes.Database.SQLiteDBHelper;
import com.lit.litnotes.Database.SQLiteDBManager;
import com.lit.litnotes.R;

public class ReminderHelper {
    public static  final String CHANNEL_ID = "LIT_NOTIFY";
    public static  final String CHANNEL_NAME = "lit notify";

    private final SQLiteDBHelper sqLiteDBHelper;
    private final SQLiteDBManager sqLiteDBManager;
    private final Context context;
    private NotificationManager notificationManager;

    public ReminderHelper(Context context) {
        this.context = context;
        sqLiteDBHelper = new SQLiteDBHelper(context);
        sqLiteDBManager = new SQLiteDBManager(context);
        sqLiteDBManager.open();
        createChannel();
    }

    public void createChannel(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getReminderHelper().createNotificationChannel(notificationChannel);
        }
    }

    @SuppressLint("RestrictedApi")
    public void notify(Integer id, String title, String body, PendingIntent pendingIntent, NotificationCompat.Action action,int type){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,CHANNEL_ID)
                    .setContentText(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.checkbox_checked)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setShowWhen(true);
            notificationBuilder.mActions.clear();
            if(type == 0) notificationBuilder.addAction(action);
            getReminderHelper().notify(id,notificationBuilder.build());
        }
    }

    public void dismissNotification(Integer id){
        getReminderHelper().cancel(id);
    }

    private NotificationManager getReminderHelper() {
        if(notificationManager == null) notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        return notificationManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void scheduleNotification(long id, int type,String title, String body, long timeInMillis){
        @SuppressLint("ServiceCast") AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context,AlarmReminder.class);
        intent.putExtra("id",id);
        intent.putExtra("type",type);
        intent.putExtra("title",title);
        intent.putExtra("body",body);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,(int) id, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void cancelScheduleNotification(long id, String body){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReminder.class);
        intent.putExtra("id", id);
        intent.putExtra("body", body);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(context,(int) id,intent,PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

}
