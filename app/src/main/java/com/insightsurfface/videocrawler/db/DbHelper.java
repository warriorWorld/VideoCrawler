package com.insightsurfface.videocrawler.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private Context xcontext;

    public static final String VIDEO_TABLE = "create table if not exists VideoTable ("
            + "id integer primary key autoincrement,"
            + "path text," + "title text," + "duration int," + "watched_time int)";
    public static final String WORDS_BOOK = "create table if not exists WordsBook ("
            + "id integer primary key autoincrement,"
            + "word text," + "time integer," + "createdtime TimeStamp NOT NULL DEFAULT (datetime('now','localtime')))";

    public DbHelper(Context context, String name, CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
        xcontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(VIDEO_TABLE);
        db.execSQL(WORDS_BOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(VIDEO_TABLE);
        db.execSQL(WORDS_BOOK);
    }
}
