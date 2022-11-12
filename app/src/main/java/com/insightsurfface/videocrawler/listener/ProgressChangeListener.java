package com.insightsurfface.videocrawler.listener;

public interface ProgressChangeListener {
    void onProgressChanged(float value);
    void onStartTrackingTouch();
    void onStopTrackingTouch();
    void onLongTouch();
    void onLongTouchUp();
}
