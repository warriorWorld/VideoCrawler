package com.insightsurfface.videocrawler.listener;

public interface ProgressChangeListener {
    void onProgressChanged(int value);
    void onStartTrackingTouch();
    void onStopTrackingTouch();
}
