package com.insightsurfface.videocrawler.bean;

import android.graphics.Bitmap;

import com.insightsurface.lib.bean.BaseBean;

/**
 * Created by Administrator on 2016-07-29.
 */
public class WordsBookBean extends BaseBean {
    private String word;
    private int time;
    private Bitmap wordBp;
    private String translate;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Bitmap getWordBp() {
        return wordBp;
    }

    public void setWordBp(Bitmap wordBp) {
        this.wordBp = wordBp;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }
}
