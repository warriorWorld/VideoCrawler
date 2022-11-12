package com.insightsurfface.videocrawler.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.insightsurface.lib.utils.SharedPreferencesUtils;

/**
 * Created by acorn on 2022/11/12.
 */
public class SPUtil extends SharedPreferencesUtils {
    private static SharedPreferences mSharedPreferences;

    public static void setSharedPreferencesData(Context mContext, String mKey, float mValue) {
        if (null == mSharedPreferences) {
            mSharedPreferences = mContext.getSharedPreferences(
                    "video", Context.MODE_PRIVATE);
        }
        mSharedPreferences.edit().putFloat(mKey, mValue).commit();
    }

    public static float getFloatSharedPreferencesData(Context mContext, String mKey) {
        return getFloatSharedPreferencesData(mContext, mKey, 0);
    }

    public static float getFloatSharedPreferencesData(Context mContext, String mKey, float defaultValue) {
        if (null == mSharedPreferences) {
            mSharedPreferences = mContext.getSharedPreferences(
                    "video", Context.MODE_PRIVATE);
        }
        return mSharedPreferences.getFloat(mKey, defaultValue);
    }
}
