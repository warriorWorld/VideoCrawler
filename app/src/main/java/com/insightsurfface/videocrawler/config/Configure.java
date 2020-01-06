package com.insightsurfface.videocrawler.config;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class Configure extends com.insightsurface.lib.config.Configure {
    public static final int DB_VERSION = 1;
    public static int currentOrientation = 90;
    final public static DisplayImageOptions smallImageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();
    public static final String YOUDAO = "http://fanyi.youdao.com/openapi.do?keyfrom=foreignnews&key=14473" +
            "94905&type=data&doctype=json&version=1.1&q=";
    public static final String QQ_GROUP = "782685214";
    public static final String DOWNLOAD_URL = "https://github.com/warriorWorld/VideoCrawler/raw/master/app/release/app-release.apk";
}
