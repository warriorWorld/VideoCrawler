package com.insightsurfface.videocrawler.widget.dragview;

import android.content.Context;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;

public class DragableImageView extends ImageView {
    boolean startDrag = false;
    boolean touchDown = false;
    Point downPoint = new Point();
    Point downViewPoint = new Point();
    int mTouchSlop = 0;
    int mCurrentX, mCurrentY;

    int marginLeft, marginTop, marginRight, marginBottom;

    int parentTop = 0;

    boolean firstTimeLayout = true;

    public DragableImageView(Context context) {
        this(context, null, 0);
    }

    public DragableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public void setDragMargins(int marginLeft, int marginTop, int marginRight,
                               int marginBottom) {
        this.marginLeft = marginLeft;
        this.marginTop = marginTop;
        this.marginRight = marginRight;
        this.marginBottom = marginBottom;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int[] location = new int[2];
        ((ViewGroup) getParent()).getLocationOnScreen(location);
        parentTop = location[1];
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY() - parentTop;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downPoint.set(x, y);
                downViewPoint.set((int) event.getX(), (int) event.getY());
                touchDown = true;
                break;
            case MotionEvent.ACTION_MOVE:
                int distanceX = x - downPoint.x;
                int distanceY = y - downPoint.y;
                if (touchDown) {
                    if (Math.abs(distanceX) > mTouchSlop
                            || Math.abs(distanceY) > mTouchSlop) {
                        if (!startDrag) {
                            startDrag = true;
                        }
                    } else {
                        // click
                    }
                }

                if (startDrag) {
                    int targetX = x - downViewPoint.x;
                    int targetY = y - downViewPoint.y;

                    targetX = Math.max(targetX, marginLeft + ((ViewGroup) getParent()).getPaddingLeft());
                    targetX = Math.min(targetX,
                            ((ViewGroup) getParent()).getRight()
                                    - ((ViewGroup) getParent()).getPaddingRight()
                                    - this.getWidth() - marginRight);
                    targetY = Math.max(targetY, marginTop
                            + ((ViewGroup) getParent()).getPaddingTop());
                    targetY = Math.min(targetY,
                            ((ViewGroup) getParent()).getBottom()
                                    - ((ViewGroup) getParent()).getPaddingBottom()
                                    - this.getHeight() - marginBottom);
                    mCurrentX = targetX;
                    mCurrentY = targetY;

                    this.layout(targetX, targetY, this.getWidth() + targetX,
                            this.getHeight() + targetY);
                }

                break;
            case MotionEvent.ACTION_UP:
                if (touchDown) {
                    if (startDrag) {
                        // drag
                    } else {
                        // click
                        performClick();
                    }
                }
                startDrag = false;
                touchDown = false;
                break;
            default:
                break;
        }
        return true;
    }

    public void setCurrentPoint(int x, int y) {
        // Do nothing
    }

    public void updateXY(int x, int y) {

        int targetX = x + getWidth() / 2 - downViewPoint.x;
        int targetY = y + getHeight() / 2 - downViewPoint.y;
        targetX = Math.max(targetX, marginLeft + ((ViewGroup) getParent()).getPaddingLeft());
        targetX = Math.min(targetX,
                ((ViewGroup) getParent()).getRight()
                        - ((ViewGroup) getParent()).getPaddingRight()
                        - this.getWidth() - marginRight);
        targetY = Math.max(targetY, marginTop
                + ((ViewGroup) getParent()).getPaddingTop());
        targetY = Math.min(targetY,
                ((ViewGroup) getParent()).getBottom()
                        - ((ViewGroup) getParent()).getPaddingBottom()
                        - this.getHeight() - marginBottom);
        mCurrentX = targetX;
        mCurrentY = targetY;

//		downPoint.set(x, y - parentTop);
//		downViewPoint.set(x, y);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        if (firstTimeLayout) {
            mCurrentX = left;
            mCurrentY = top;
            firstTimeLayout = false;
        }
        if (mCurrentX == left && mCurrentY == top) {
            super.onLayout(changed, left, top, right, bottom);
        } else {
            layout(mCurrentX, mCurrentY, this.getWidth() + mCurrentX,
                    this.getHeight() + mCurrentY);
        }
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        Log.v("TAG", "onSaveInstanceState");
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.v("TAG", "onRestoreInstanceState");
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        mCurrentX = ss.mCurrentX;
        mCurrentY = ss.mCurrentY;
        super.onRestoreInstanceState(ss.getSuperState());
        Log.v("TAG", "onRestoreInstanceState");
    }

    /**
     * User interface state that is stored by TextView for implementing
     * {@link View#onSaveInstanceState}.
     */
    public static class SavedState extends BaseSavedState {
        int mCurrentX;
        int mCurrentY;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mCurrentX);
            out.writeInt(mCurrentY);
        }

        @SuppressWarnings("hiding")
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        private SavedState(Parcel in) {
            super(in);
            mCurrentX = in.readInt();
            mCurrentY = in.readInt();
        }
    }

}
