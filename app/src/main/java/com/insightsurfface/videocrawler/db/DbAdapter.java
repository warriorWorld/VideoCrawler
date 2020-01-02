package com.insightsurfface.videocrawler.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.insightsurfface.videocrawler.business.bean.VideoBean;
import com.insightsurfface.videocrawler.business.bean.WordsBookBean;
import com.insightsurfface.videocrawler.config.Configure;

import java.util.ArrayList;


public class DbAdapter {
    public static final String DB_NAME = "video.db";
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    public DbAdapter(Context context) {
        dbHelper = new DbHelper(context, DB_NAME, null, Configure.DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 插入一条书籍信息
     */
    public void insertVideoTableTb(String path, String videoName, int duration, int watchedTime) {
        if (queryadded(path)) {
            return;
        }
        db.execSQL(
                "insert into VideoTable (path,title,duration,watched_time) values (?,?,?,?)",
                new Object[]{path, videoName, duration, watchedTime});
    }

    /**
     * 插入一条生词信息
     */
    public void insertWordsBookTb(String word) {
        int time = queryQueryedTime(word);
        if (time > 0) {
            //如果查过这个单词 那就update 并且time+1
            time++;
            updateTimeTOWordsBook(word, time);
        } else {
            db.execSQL(
                    "insert into WordsBook (word,time) values (?,?)",
                    new Object[]{word, 1});
        }
    }

    /**
     * 更新进度信息
     */
    public void updateProgressTOVideoTb(String path, int whatchedTime) {
        db.execSQL("update VideoTable set watched_time=? where path=?",
                new Object[]{whatchedTime, path});
    }

    /**
     * 更新生词信息
     */
    public void updateTimeTOWordsBook(String word, int time) {
        db.execSQL("update WordsBook set time=? where word=?",
                new Object[]{time, word});
    }

    /**
     * 查询所有书籍
     *
     * @return
     */
    public ArrayList<VideoBean> queryAllVideos() {
        ArrayList<VideoBean> resBeans = new ArrayList<VideoBean>();
        Cursor cursor = db
                .query("VideoTable", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("title"));
            String path = cursor
                    .getString(cursor.getColumnIndex("path"));
            int id = cursor
                    .getInt(cursor.getColumnIndex("id"));
            int duration = cursor
                    .getInt(cursor.getColumnIndex("duration"));
            int watchedTime = cursor
                    .getInt(cursor.getColumnIndex("watched_time"));
            VideoBean item = new VideoBean();
            item.setId(id);
            item.setPath(path);
            item.setTitle(name);
            item.setDuration(duration);
            item.setWatched_time(watchedTime);
            resBeans.add(item);
        }
        cursor.close();
        return resBeans;
    }

    /**
     * 查询所有生词
     *
     * @return
     */
    public ArrayList<WordsBookBean> queryAllWordsBook() {
        ArrayList<WordsBookBean> resBeans = new ArrayList<WordsBookBean>();
        Cursor cursor = db
                .query("WordsBook", null, null, null, null, null, "createdtime desc");

        while (cursor.moveToNext()) {
            String word = cursor.getString(cursor.getColumnIndex("word"));
            int time = cursor
                    .getInt(cursor.getColumnIndex("time"));
            WordsBookBean item = new WordsBookBean();
            item.setWord(word);
            item.setTime(time);
            resBeans.add(item);
        }
        cursor.close();
        return resBeans;
    }

    /**
     * 查询是否已经添加过
     */
    public boolean queryadded(String path) {
        Cursor cursor = db.rawQuery(
                "select title from VideoTable where path=?",
                new String[]{path});
        int count = cursor.getCount();
        cursor.close();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询是否查询过
     */
    public boolean queryQueryed(String word) {
        Cursor cursor = db.rawQuery(
                "select word from WordsBook where word=?",
                new String[]{word});
        int count = cursor.getCount();
        cursor.close();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public int queryQueryedTime(String word) {
        int res = 0;
        Cursor cursor = db.rawQuery(
                "select time from WordsBook where word=?",
                new String[]{word});
        int count = cursor.getCount();
        if (count > 0) {
            while (cursor.moveToNext()) {
                res = cursor.getInt(cursor.getColumnIndex("time"));
            }
        }
        cursor.close();
        return res;
    }

    /**
     * 删除视频
     */
    public void deleteVideoByPath(String path) {
        db.execSQL("delete from VideoTable where path=?",
                new Object[]{path});
    }

    /**
     * 删除生词
     */
    public void deleteWordByWord(String word) {
        db.execSQL("delete from WordsBook where word=?",
                new Object[]{word});
    }

    public void closeDb() {
        if (null != db) {
            db.close();
        }
    }
}
