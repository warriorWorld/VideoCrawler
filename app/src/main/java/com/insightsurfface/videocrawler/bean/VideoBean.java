package com.insightsurfface.videocrawler.bean;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.insightsurface.lib.bean.BaseBean;

public class VideoBean extends BaseBean implements Parcelable {
    private int id;
    private String title;
    private String path;
    private int duration;
    private int watched_time;
    private Bitmap thumbnail;

    public VideoBean() {

    }

    protected VideoBean(Parcel in) {
        id = in.readInt();
        title = in.readString();
        path = in.readString();
        duration = in.readInt();
        watched_time = in.readInt();
        thumbnail = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<VideoBean> CREATOR = new Creator<VideoBean>() {
        @Override
        public VideoBean createFromParcel(Parcel in) {
            return new VideoBean(in);
        }

        @Override
        public VideoBean[] newArray(int size) {
            return new VideoBean[size];
        }
    };

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

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(path);
        dest.writeInt(duration);
        dest.writeInt(watched_time);
        dest.writeParcelable(thumbnail, flags);
    }
}
