package com.insightsurfface.videocrawler.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DisplayUtil extends com.insightsurface.lib.utils.DisplayUtil {
    /**
     * 获取屏幕高度 適配全面屏高度
     *
     * @return
     */
    public static int getScreenRealWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            Point point = new Point();
            wm.getDefaultDisplay().getRealSize(point);
            return point.x;
        } else {
            return 0;
        }
    }

    /**
     * 获取屏幕高度 適配全面屏高度
     *
     * @return
     */
    public static int getScreenRealHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            Point point = new Point();
            wm.getDefaultDisplay().getRealSize(point);
            return point.y;
        } else {
            return 0;
        }
    }

    public static boolean isPad(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        // 屏幕宽度
        float screenWidth = display.getWidth();
        // 屏幕高度
        float screenHeight = display.getHeight();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        // 屏幕尺寸
        double screenInches = Math.sqrt(x + y);
        // 大于6尺寸则为Pad
        return screenInches >= 7.5;
    }
}
