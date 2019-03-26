package com.jiuj.servicemotor.Database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class myDBClass extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "servicemotor.db";
    public static final int DATABASE_VERSION = 1;
    public myDBClass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table db_service('noref' text, 'judul' text, 'tempat' text, 'keterangan' text,'motortype' text,'image' text, 'tglservice' datetime DEFAULT (datetime('now','localtime')), 'createtime' datetime DEFAULT (datetime('now','localtime')))");
    }

    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
