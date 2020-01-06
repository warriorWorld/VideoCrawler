package com.insightsurfface.videocrawler.utils;

public class FastClickUtil {
    private static final int CLICK_DELAY_TIME = 500;
    private static long lastClickTime;

    public static boolean isNotFastClick() {
        boolean flag = false;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = currentClickTime;
        return flag;
    }
}
