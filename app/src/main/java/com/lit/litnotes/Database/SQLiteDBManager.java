package com.lit.litnotes.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteDBManager {

    private  Context context;
    private SQLiteDBHelper sqLiteDBHelper;
    private SQLiteDatabase sqLiteDatabase;

    public SQLiteDBManager(Context context){
        this.context = context;
    }

    public SQLiteDBManager open() throws SQLException {
        sqLiteDBHelper = new SQLiteDBHelper(context);
        sqLiteDatabase = sqLiteDBHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        sqLiteDBHelper.close();
    }

    public int insert(String tr_name, ContentValues contentValues){
        return (int) sqLiteDatabase.insert(tr_name,null,contentValues);
    }

    public Cursor fetch(String query){
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        return cursor;
    }

    public int update(ContentValues contentValues, String table_name, String condition ){
        return sqLiteDatabase.update(table_name,contentValues,condition,null);
    }

    public void delete(String table_name, String condition){
        sqLiteDatabase.delete(table_name,condition,null);
    }
}
