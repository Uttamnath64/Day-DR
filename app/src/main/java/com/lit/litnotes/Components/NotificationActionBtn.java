package com.lit.litnotes.Components;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.lit.litnotes.Database.SQLiteDBHelper;
import com.lit.litnotes.Database.SQLiteDBManager;
import com.lit.litnotes.Database.Task;

public class NotificationActionBtn extends BroadcastReceiver {
    SQLiteDBManager sqLiteDBManager;
    SQLiteDBHelper sqLiteDBHelper;
    @Override
    public void onReceive(Context context, Intent intent) {
        sqLiteDBHelper = new SQLiteDBHelper(context);
        sqLiteDBManager = new SQLiteDBManager(context);
        sqLiteDBManager.open();

        ContentValues taskValue = new ContentValues();
        taskValue.put("Checked",1);
        sqLiteDBManager.update(taskValue, SQLiteDBHelper.TR_TASK,"R_Id = '"+intent.getLongExtra("id",0)+"'");

        new ReminderHelper(context).dismissNotification((int) intent.getLongExtra("id",0));
    }
}
