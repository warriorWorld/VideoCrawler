package com.insightsurfface.videocrawler.utils;

import android.content.Context;
import android.graphics.Point;
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
}
