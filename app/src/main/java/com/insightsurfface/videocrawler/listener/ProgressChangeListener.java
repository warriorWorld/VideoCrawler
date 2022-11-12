package com.insightsurfface.videocrawler.listener;

public interface ProgressChangeListener {
    void onProgressChanged(float value);

    void onStartTrackingTouch();

    void onStopTrackingTouch();

    void onProgressChangedVertical(float value);

    void onStartTrackingTouchVertical();

    void onStopTrackingTouchVertical();

    void onLongTouch();

    void onLongTouchUp();
}
