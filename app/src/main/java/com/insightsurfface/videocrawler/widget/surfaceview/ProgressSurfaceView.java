package com.insightsurfface.videocrawler.widget.surfaceview;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.insightsurface.lib.utils.Logger;
import com.insightsurfface.videocrawler.listener.ProgressChangeListener;

public class ProgressSurfaceView extends SurfaceView {
    private Context mContext;
    private Point downPoint = new Point();
    private Point downViewPoint = new Point();
    private boolean startDrag = false;
    private boolean touchDown = false;
    private boolean trackingTouched = false;
    private int LONG_TOUCH_THRESHOLD = 600;
    private int mTouchSlop = 0, mTrackingTouchSlop;
    private ProgressChangeListener mProgressChangeListener;
    private int width, height;
    private Handler longTouchHandler = new Handler(Looper.getMainLooper());
    private boolean longTouchTriggered = false;

    public ProgressSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public ProgressSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProgressSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mTrackingTouchSlop = mTouchSlop * 2;
        post(new Runnable() {
            @Override
            public void run() {
                width = getMeasuredWidth();
                height = getMeasuredHeight();
            }
        });
    }

    private void handleLongTouch() {
        longTouchHandler.removeCallbacksAndMessages(null);
        longTouchHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                longTouchTriggered = true;
                if (null != mProgressChangeListener) {
                    mProgressChangeListener.onLongTouch();
                }
            }
        }, LONG_TOUCH_THRESHOLD);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downPoint.set(x, y);
                downViewPoint.set((int) event.getX(), (int) event.getY());
                touchDown = true;
                longTouchTriggered = false;
                handleLongTouch();
                break;
            case MotionEvent.ACTION_MOVE:
                int distanceX = x - downPoint.x;
                int distanceY = y - downPoint.y;
                if (touchDown) {
                    if (Math.abs(distanceX) > mTouchSlop) {
                        longTouchHandler.removeCallbacksAndMessages(null);
                        if (!startDrag) {
                            startDrag = true;
                        }
                        if (Math.abs(distanceX) > mTrackingTouchSlop
                                && !trackingTouched) {
                            if (null != mProgressChangeListener) {
                                mProgressChangeListener.onStartTrackingTouch();
                            }
                            trackingTouched = true;
                        }
                        if (null != mProgressChangeListener) {
                            mProgressChangeListener.onProgressChanged(Float.valueOf(distanceX) / Float.valueOf(width));
                        }
                    } else {
                        // click
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                longTouchHandler.removeCallbacksAndMessages(null);
                if (touchDown) {
                    if (startDrag) {
                        // drag
                        if (null != mProgressChangeListener) {
                            mProgressChangeListener.onStopTrackingTouch();
                        }
                        if (longTouchTriggered) {
                            if (null != mProgressChangeListener) {
                                mProgressChangeListener.onLongTouchUp();
                            }
                        }
                    } else {
                        // click
                        if (longTouchTriggered) {
                            if (null != mProgressChangeListener) {
                                mProgressChangeListener.onLongTouchUp();
                            }
                        } else {
                            performClick();
                        }
                    }
                }
                startDrag = false;
                touchDown = false;
                trackingTouched = false;
                longTouchTriggered = false;
                break;
            default:
                break;
        }
        return true;
    }

    public void setProgressChangeListener(ProgressChangeListener progressChangeListener) {
        mProgressChangeListener = progressChangeListener;
    }
}
