package com.insightsurfface.videocrawler.business.bean;

import com.insightsurface.lib.bean.BaseBean;

public class VideoBean extends BaseBean {
    private int id;
    private String title;
    private String path;
    private int duration;
    private int watched_time;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getWatched_time() {
        return watched_time;
    }

    public void setWatched_time(int watched_time) {
        this.watched_time = watched_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
