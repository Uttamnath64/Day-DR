package com.lit.litnotes.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDBHelper extends SQLiteOpenHelper {

    static final String DB_NAME = "LIT.NOTES.DB";
    static final int DB_VERSION = 1;

    public static final String TR_NOTE = "tr_note";
    public static final String TR_LIST = "tr_list";
    public static final String TR_NOTIFY = "tr_notify";
    public static final String TR_TASK = "tr_task";
    public static final String TR_TABLE = "tr_table";


    public SQLiteDBHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION );
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+TR_NOTE+" (Id INTEGER PRIMARY KEY AUTOINCREMENT, List_Id int, Title Varchar(100) NOT NULL,Color_Id int, Description TEXT, UDateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, DateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP )");
        sqLiteDatabase.execSQL("CREATE TABLE "+TR_LIST+" (Id INTEGER PRIMARY KEY AUTOINCREMENT, Title TEXT NOT NULL, UDateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TR_TASK+" (Id INTEGER PRIMARY KEY AUTOINCREMENT, Text TEXT NOT NULL, R_Id int, Checked int, UDateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, DateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TR_TABLE+" (Id INTEGER PRIMARY KEY AUTOINCREMENT, Title TEXT NOT NULL, Day int, R_Id int, UDateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, DateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TR_NOTIFY+" (Id INTEGER PRIMARY KEY AUTOINCREMENT, Type int, RDate varchar(10),RTime varchar(5))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TR_NOTE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TR_LIST);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TR_NOTIFY);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TR_TASK);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TR_TABLE);
    }
}
