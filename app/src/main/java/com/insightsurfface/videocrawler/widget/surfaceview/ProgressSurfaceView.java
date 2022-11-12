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

    private enum DragType {
        INIT,
        VERTICAL,
        HORIZONTAL
    }

    private DragType mDragType = DragType.INIT;

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
                mDragType = DragType.INIT;
                handleLongTouch();
                break;
            case MotionEvent.ACTION_MOVE:
                int distanceX = x - downPoint.x;
                int distanceY = downPoint.y - y;//从人的角度决定哪边是上
                if (touchDown) {
                    if (Math.abs(distanceX) > mTouchSlop && mDragType != DragType.VERTICAL) {
                        //横向滑动
                        longTouchHandler.removeCallbacksAndMessages(null);
                        if (!startDrag) {
                            startDrag = true;
                        }
                        if (Math.abs(distanceX) > mTrackingTouchSlop
                                && !trackingTouched) {
                            //每次down后首次触发决定了之后只关心横向或竖向
                            mDragType = DragType.HORIZONTAL;
                            if (null != mProgressChangeListener) {
                                mProgressChangeListener.onStartTrackingTouch();
                            }
                            trackingTouched = true;
                        }
                        if (null != mProgressChangeListener) {
//                            Logger.d("distance x:"+distanceX+"/"+width+"/"+getMeasuredWidth());
                            mProgressChangeListener.onProgressChanged(Float.valueOf(distanceX) / Float.valueOf(getMeasuredWidth()));
                        }
                    } else if (Math.abs(distanceY) > mTouchSlop && mDragType != DragType.HORIZONTAL) {
                        //纵向滑动
                        longTouchHandler.removeCallbacksAndMessages(null);
                        if (!startDrag) {
                            startDrag = true;
                        }
                        if (Math.abs(distanceY) > mTrackingTouchSlop
                                && !trackingTouched) {
                            //每次down后首次触发决定了之后只关心横向或竖向
                            mDragType = DragType.VERTICAL;
                            if (null != mProgressChangeListener) {
                                mProgressChangeListener.onStartTrackingTouchVertical();
                            }
                            trackingTouched = true;
                        }
                        if (null != mProgressChangeListener) {
//                            Logger.d("distance y:"+distanceY+"/"+height+"/"+getMeasuredHeight());
                            mProgressChangeListener.onProgressChangedVertical(Float.valueOf(distanceY) / Float.valueOf(getMeasuredHeight()));
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
                            switch (mDragType) {
                                case INIT:
                                    break;
                                case VERTICAL:
                                    mProgressChangeListener.onStopTrackingTouchVertical();
                                    break;
                                case HORIZONTAL:
                                    mProgressChangeListener.onStopTrackingTouch();
                                    break;
                            }
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
                mDragType = DragType.INIT;
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
