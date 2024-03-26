package com.insightsurfface.videocrawler.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;

import com.insightsurface.lib.utils.BitmapUtils;
import com.insightsurface.lib.utils.ShareObjUtil;
import com.insightsurface.lib.utils.SharedPreferencesUtils;
import com.insightsurfface.videocrawler.bean.VideoBean;
import com.insightsurfface.videocrawler.bean.WordsBookBean;
import com.insightsurfface.videocrawler.config.Configure;
import com.insightsurfface.videocrawler.config.ShareKeys;
import com.insightsurfface.videocrawler.utils.BitmapUtil;
import com.insightsurfface.videocrawler.utils.VideoUtil;

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
    public void insertVideoTableTb(Context context, String path, String videoName, int duration, int watchedTime) {
        if (queryadded(path)) {
            return;
        }
        db.execSQL(
                "insert into VideoTable (path,title,duration,watched_time) values (?,?,?,?)",
                new Object[]{path, videoName, duration, watchedTime});
        String bitmpString = "";
        try {
            bitmpString = BitmapUtils.bitmapToString(VideoUtil.getVideoThumbnail(context, Uri.parse(path)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ShareObjUtil.saveObject(context, bitmpString, queryIdByPath(path) + ShareKeys.VIDEO_THUMBNAIL);
    }

    /**
     * 插入一条生词信息
     */
    public void insertWordsBookTb(Context context, String word, String translate, Bitmap bp) {
        ShareObjUtil.saveObject(context, BitmapUtils.bitmapToString(bp, false), word + ShareKeys.WORD_BITMAP);
        int time = queryQueryedTime(word);
        if (time > 0) {
            //如果查过这个单词 那就update 并且time+1
            time++;
            updateTimeTOWordsBook(word, time);
        } else {
            db.execSQL(
                    "insert into WordsBook (word,time,translate) values (?,?,?)",
                    new Object[]{word, 1, translate});
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
    public ArrayList<VideoBean> queryAllVideos(Context context) {
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
            Bitmap bp = BitmapUtil.stringToBitmap((String) ShareObjUtil.getObject(context, id + ShareKeys.VIDEO_THUMBNAIL));
            VideoBean item = new VideoBean();
            item.setId(id);
            item.setPath(path);
            item.setTitle(name);
            item.setDuration(duration);
            item.setWatched_time(watchedTime);
            item.setThumbnail(bp);
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
    public ArrayList<WordsBookBean> queryAllWordsBook(Context context) {
        ArrayList<WordsBookBean> resBeans = new ArrayList<WordsBookBean>();
        Cursor cursor = db
                .query("WordsBook", null, null, null, null, null, "createdtime desc");
        long currentTime = System.currentTimeMillis();
        int killPeriod = SharedPreferencesUtils.getIntSharedPreferencesData(context, ShareKeys.KILL_PERIOD_KEY, 6);
        int minGapTime = killPeriod * 60 * 60 * 1000;


        while (cursor.moveToNext()) {
            String word = cursor.getString(cursor.getColumnIndex("word"));
            String translate = cursor.getString(cursor.getColumnIndex("translate"));
            int time = cursor
                    .getInt(cursor.getColumnIndex("time"));
            Bitmap bp = BitmapUtil.stringToBitmap((String) ShareObjUtil.getObject(context, word + ShareKeys.WORD_BITMAP));
            long lastKillTime = 0;
            try {
                lastKillTime = cursor.getLong(cursor.getColumnIndex("update_time"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            int killTime = 0;
            try {
                killTime = cursor.getInt(cursor.getColumnIndex("kill_time"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //随着kill次数 间隔时长乘数递增
            long minTime = currentTime - minGapTime * killTime;
            if (killTime > 0 && minTime < lastKillTime) {
                //不显示 kill过的并且时间未超过时长的
            } else {
                WordsBookBean item = new WordsBookBean();
                item.setWord(word);
                item.setTime(time);
                item.setTranslate(translate);
                item.setWordBp(bp);
                resBeans.add(item);
            }
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

    public int queryIdByPath(String path) {
        int res = 0;
        Cursor cursor = db.rawQuery(
                "select id from VideoTable where path=?",
                new String[]{path});
        int count = cursor.getCount();
        if (count > 0) {
            while (cursor.moveToNext()) {
                res = cursor.getInt(cursor.getColumnIndex("id"));
            }
        }
        cursor.close();
        return res;
    }

    /**
     * 删除视频
     */
    public void deleteVideoById(Context context, int id) {
        db.execSQL("delete from VideoTable where id=?",
                new Object[]{id});
        ShareObjUtil.deleteFile(context, id + ShareKeys.VIDEO_THUMBNAIL);
    }

    public int queryKilledTime(String word) {
        int res = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "select kill_time from WordsBook where word=?",
                    new String[]{word});
            int count = cursor.getCount();
            if (count > 0) {
                while (cursor.moveToNext()) {
                    res = cursor.getInt(cursor.getColumnIndex("kill_time"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return res;
    }

    public void killWordByWord(Context context, String word) {
        int time = queryKilledTime(word);
        time++;
        int killableTime = SharedPreferencesUtils.getIntSharedPreferencesData(context, ShareKeys.KILLABLE_TIME_KEY, 3);
        if (time >= killableTime) {
            deleteWordByWord(context, word);
        } else {
            db.execSQL("update WordsBook set kill_time=?,update_time=? where word=?",
                    new Object[]{time, System.currentTimeMillis(), word});
        }
    }

    /**
     * 删除生词
     */
    public void deleteWordByWord(Context context, String word) {
        db.execSQL("delete from WordsBook where word=?",
                new Object[]{word});
        ShareObjUtil.deleteFile(context, word + ShareKeys.WORD_BITMAP);
    }

    public void closeDb() {
        if (null != db) {
            db.close();
        }
    }
}
