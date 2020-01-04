package com.insightsurfface.videocrawler.widget.dragview;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.insightsurface.lib.config.ShareKeys;
import com.insightsurface.lib.utils.Logger;
import com.insightsurface.lib.utils.SharedPreferencesUtils;
import com.insightsurface.lib.utils.VibratorUtil;
import com.insightsurfface.videocrawler.utils.DisplayUtil;

import androidx.annotation.Nullable;


@SuppressLint("AppCompatCustomView")
public class ShelterView extends ImageView {
    private String TAG = "DragImageView";
    private Context mContext;
    private float downX;
    private float downY;
    private long downTime;
    private int lastMotion, lastLeft, lastTop, screenWidth, screenHeight;
    private boolean edged = false;
    private int marginBottom;
    private int clickThreshold = 1;
    private float xDistance, yDistance;
    private boolean savePosition = false;
    private OnClickListener mOnClickListener;
    private String lastPosKey = ShareKeys.LAST_DRAGVIEW_POSITION;
    private OnDragListener mOnDragListener;

    public ShelterView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ShelterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public ShelterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        screenWidth = DisplayUtil.getScreenRealHeight(mContext);
        screenHeight = DisplayUtil.getScreenWidth(mContext);
        marginBottom = DisplayUtil.dip2px(mContext, 0);
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            if ((lastMotion == MotionEvent.ACTION_UP || lastMotion == MotionEvent.ACTION_CANCEL) && edged) {
                this.layout(lastLeft, lastTop, lastLeft+getWidth(), lastTop+getHeight());
                return;
            }
            lastLeft = left;
            lastTop = top;
        }
//        Logger.d(TAG+":"+changed+","+left+","+top+","+right+","+bottom);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
//        super.setOnClickListener(l);
        mOnClickListener = l;
    }

    private void savePosition() {
        if (savePosition) {
            String save = 0 + ";" + (int) lastTop + ";";
            SharedPreferencesUtils.setSharedPreferencesData(mContext, lastPosKey, save);
        }
    }

    public void toLastPosition() {
        String s = SharedPreferencesUtils.getSharedPreferencesData(mContext, lastPosKey);
        if (TextUtils.isEmpty(s)) {
            return;
        }
        String[] ss = s.split(";");
        if (ss.length <= 0) {
            return;
        }

        try {
            this.layout(Integer.valueOf(ss[0]), Integer.valueOf(ss[1]), Integer.valueOf(ss[0])+getWidth(), Integer.valueOf(ss[1])+getHeight());
            //阻止onlayout后被重置位置
            lastMotion = MotionEvent.ACTION_UP;
            edged = true;

//            setX(Float.valueOf(ss[0]));
//            setY(Float.valueOf(ss[1]));

//            ObjectAnimator animator = ObjectAnimator.ofFloat(this, "translationX", getTranslationX(), Integer.valueOf(ss[0]));
//            animator.setDuration(250);
//            animator.start();
//            ObjectAnimator animator1 = ObjectAnimator.ofFloat(this, "translationY", getTranslationY(), Integer.valueOf(ss[1]) - getY());
//            animator1.setDuration(250);
//            animator1.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toEdge() {
        savePosition();
        edged = true;
        if (null!=mOnDragListener){
            mOnDragListener.dragEnd();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Logger.d(TAG+":onTouchEvent");
        super.onTouchEvent(event);
        if (this.isEnabled()) {
            edged = false;
            lastMotion = event.getAction();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    VibratorUtil.Vibrate(mContext, 30);
                    downX = event.getX();
                    downY = event.getY();
                    xDistance = 0;
                    yDistance = 0;
                    if (null!=mOnDragListener){
                        mOnDragListener.dragStart();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    xDistance = event.getX() - downX;
                    yDistance = event.getY() - downY;
                    Logger.d("xDistance:" + xDistance + "      yDistance:" + yDistance);
                    if (xDistance != 0 && yDistance != 0) {
                        int l = (int) (getLeft() + xDistance);
                        int r = (int) (getRight() + xDistance);
                        int t = (int) (getTop() + yDistance);
                        int b = (int) (getBottom() + yDistance);
                        if (l < 0) {
                            l = 0;
                            r = (int) (getRight() - getLeft());
                        }
                        if (r > screenWidth) {
                            r = screenWidth;
                            l = (int) (getLeft() - getRight() + screenWidth);
                        }
                        if (t < 0) {
                            t = 0;
                            b = (int) (getBottom() - getTop());
                        }
                        if (b > screenHeight - marginBottom) {
                            b = screenHeight - marginBottom;
                            t = (int) (getTop() - getBottom() + screenHeight - marginBottom);
                        }
                        this.layout(l, t, r, b);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(xDistance) <= clickThreshold && Math.abs(yDistance) <= clickThreshold && null != mOnClickListener) {
                        mOnClickListener.onClick(this);
                    }
                    setPressed(false);
                    toEdge();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    setPressed(false);
                    toEdge();
                    break;
            }
            return true;
        }
        return false;
    }


    public void setSavePosition(boolean savePosition) {
        this.savePosition = savePosition;
    }

    public String getLastPosKey() {
        return lastPosKey;
    }

    public void setLastPosKey(String lastPosKey) {
        this.lastPosKey = lastPosKey;
    }

    public void setOnDragListener(OnDragListener onDragListener) {
        mOnDragListener = onDragListener;
    }

    public interface OnDragListener {
        void dragStart();

        void dragEnd();
    }
}
