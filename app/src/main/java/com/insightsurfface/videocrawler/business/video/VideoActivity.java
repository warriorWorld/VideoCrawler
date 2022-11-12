package com.insightsurfface.videocrawler.business.video;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.SubtitleData;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.insightsurface.lib.utils.Logger;
import com.insightsurface.lib.utils.SharedPreferencesUtils;
import com.insightsurfface.stylelibrary.keyboard.English26KeyBoardView;
import com.insightsurfface.videocrawler.R;
import com.insightsurfface.videocrawler.base.BaseActivity;
import com.insightsurfface.videocrawler.bean.YoudaoResponse;
import com.insightsurfface.videocrawler.config.Configure;
import com.insightsurfface.videocrawler.config.ShareKeys;
import com.insightsurfface.videocrawler.db.DbAdapter;
import com.insightsurfface.videocrawler.listener.OnEditResultListener;
import com.insightsurfface.videocrawler.listener.ProgressChangeListener;
import com.insightsurfface.videocrawler.utils.DisplayUtil;
import com.insightsurfface.videocrawler.utils.FastClickUtil;
import com.insightsurfface.videocrawler.utils.SPUtil;
import com.insightsurfface.videocrawler.utils.ScreenShot;
import com.insightsurfface.videocrawler.utils.StringUtil;
import com.insightsurfface.videocrawler.utils.VideoUtil;
import com.insightsurfface.videocrawler.volley.VolleyCallBack;
import com.insightsurfface.videocrawler.volley.VolleyTool;
import com.insightsurfface.videocrawler.widget.dialog.ImgLandsacpeKeyboardDialog;
import com.insightsurfface.videocrawler.widget.dialog.MangaImgEditDialog;
import com.insightsurfface.videocrawler.widget.dialog.OnlyEditDialog;
import com.insightsurfface.videocrawler.widget.dialog.TranslateDialog;
import com.insightsurfface.videocrawler.widget.dragview.ShelterView;
import com.insightsurfface.videocrawler.widget.surfaceview.ProgressSurfaceView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

public class VideoActivity extends BaseActivity implements SurfaceHolder.Callback,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnVideoSizeChangedListener,
        View.OnClickListener,
        SensorEventListener {
    //    private VideoView crawlerVv;
    private ProgressSurfaceView videoSv;
    private String testURL = "http://ugccsy.qq.com/uwMROfz2r5zBIaQXGdGnC2dfJ6nAzEoO290Tsw9-2jpi_xWl/e3045nq422n.p701.1.mp4?sdtfrom=v1104&guid=4c68826f6ff46a643a05b409826286dd&vkey=B71428DD4372FA1C4D17F2139A5C470AF29469EBA261C6D167C28F10BBCF665B25C3F1AB1FC5C0C30A3E38E4ABE89ABE289D1DA018BBBE911B4925AD383DC3DB65796F0637199E68D8617FCFDA5BBDC19875A49FBC7912578A28DC8C441BDC47B7E48DC5B585051F43E13B2C0B63254E4DA1F356D92587BE2C16608CFDAB21EC";
    private Button chooseUriBtn;
    private MediaPlayer mPlayer;
    private String url;
    private SurfaceHolder mSurfaceHolder;
    private int videoWidth = 0, videoHeight = 0;
    private int screenWidth = 0, screenHeight = 0;
    private TextView titleTv;
    private DiscreteSeekBar progressSb;
    private TextView timeTv;
    private ImageView fullScreenIv;
    private View divideV;
    public static final int UPDATE_TIME = 0x0001;
    public static final int HIDE_CONTROL = 0x0002;
    public static final int RELOCATION_PROGRESS = 0x0003;
    private int lastPauseLocation = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TIME:
                    updateTime();
                    mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 500);
                    break;
                case HIDE_CONTROL:
                    hideControl();
                    break;
                case RELOCATION_PROGRESS:
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            mPlayer.seekTo(lastPauseLocation, MediaPlayer.SEEK_CLOSEST);
                        }
                        Logger.d("pause:  " + lastPauseLocation + "  start:  " + mPlayer.getCurrentPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    private ImageView playIv;
    private String title;
    private int duration = 0;
    private int finalPosition = 0;
    private int id;
    private ShelterView shelterDv;
    private ClipboardManager clip;//复制文本用
    private TranslateDialog translateResultDialog;
    private OnlyEditDialog searchDialog;
    private ImageView translateIv;
    private SensorManager sManager;
    private Sensor mSensorAccelerometer;
    private ImageView backIv, forwardIv, centerPlayIv;
    private int jumpGap, shelterHeight;
    private Group controlGroup, centerControlGroup;
    private boolean userBannedShelter = false;
    private boolean isPrepared = false;
    private DbAdapter db;//数据库
    private TextView playSpeedTv;
    private TextView sppeedTv200;
    private TextView sppeedTv175;
    private TextView sppeedTv150;
    private TextView sppeedTv125;
    private TextView sppeedTv100;
    private TextView sppeedTv75;
    private TextView sppeedTv50;
    private View speedCl;
    private boolean isUserControlling = false;
    private Handler doubleClickHandler = new Handler(Looper.getMainLooper());
    private int tryDoubleClick = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        if (TextUtils.isEmpty(url)) {
            url = intent.getDataString();
        }
        jumpGap = SharedPreferencesUtils.getIntSharedPreferencesData(this, ShareKeys.JUMP_FRAME_GAP, -1);
        shelterHeight = SharedPreferencesUtils.getIntSharedPreferencesData(this, ShareKeys.SHELTER_HEIGHT, -1);
        super.onCreate(savedInstanceState);
        db = new DbAdapter(this);
        clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        screenWidth = DisplayUtil.getScreenWidth(this);
        screenHeight = DisplayUtil.getScreenRealHeight(this);

        initSurfaceView();
        initPlayer();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarFullTransparent();
            setFitSystemWindow(false);
        }
        hideBaseTopBar();
        initSensorManager();
    }

    private void initSensorManager() {
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorAccelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public static void startActivity(Context context, int id, String fileUrl, String title) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("url", fileUrl);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    private void initSurfaceView() {
        videoSv.setZOrderOnTop(false);
        videoSv.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        videoSv.getHolder().addCallback(this);
    }

    private void initPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setScreenOnWhilePlaying(true);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnInfoListener(this);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnSeekCompleteListener(this);
        mPlayer.setOnVideoSizeChangedListener(this);
        float volume = Float.valueOf(SPUtil.getIntSharedPreferencesData(this, ShareKeys.VOLUME_KEY)) / 100f;
        mPlayer.setVolume(volume, volume);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mPlayer.setOnSubtitleDataListener(new MediaPlayer.OnSubtitleDataListener() {
                @Override
                public void onSubtitleData(@NonNull MediaPlayer mp, @NonNull SubtitleData data) {
                    Logger.d("onSubtitleData" + data.toString());
                }
            });
        }
        try {
            //使用手机本地视频
            mPlayer.setDataSource(this, Uri.parse(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新播放时间
     */
    private void updateTime() {
        if (null == mPlayer) {
            return;
        }
        if (isUserControlling) {
            return;
        }
        try {
            progressSb.setProgress(mPlayer.getCurrentPosition());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initUI() {
        super.initUI();
        videoSv = findViewById(R.id.video_sv);
        videoSv.setProgressChangeListener(new ProgressChangeListener() {
            @Override
            public void onProgressChanged(float value) {
                int pos = lastPauseLocation + (int) (duration * value);
                Logger.d("sv:" + value + " last:" + lastPauseLocation + " duration:" + duration + " ,pos:" + pos);
                if (pos > duration || pos < 0) {
                    return;
                }
                progressSb.setProgress(pos);
            }

            @Override
            public void onStartTrackingTouch() {
                isUserControlling = true;
                playPause();
                showControl();
                centerControlGroup.setVisibility(View.GONE);
            }

            @Override
            public void onStopTrackingTouch() {
                isUserControlling = false;
                playStart();
                hideControl();
            }
        });
        chooseUriBtn = findViewById(R.id.choose_uri_btn);
        controlGroup = findViewById(R.id.control_group);
        controlGroup.setVisibility(View.GONE);
        centerControlGroup = findViewById(R.id.center_control_group);
        centerControlGroup.setVisibility(View.GONE);
        shelterDv = findViewById(R.id.shelter_dv);
        shelterDv.setSavePosition(true);
        shelterDv.setLastPosKey("POS_KEY");
        shelterDv.bringToFront();
        shelterDv.setOnDragListener(new ShelterView.OnDragListener() {
            @Override
            public void dragStart() {
            }

            @Override
            public void dragEnd() {
            }
        });
        titleTv = (TextView) findViewById(R.id.video_title_tv);
        progressSb = (DiscreteSeekBar) findViewById(R.id.progress_sb);
        progressSb.setMin(0);
        progressSb.setNumericTransformer(new SeekbarTransformer());
        progressSb.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                finalPosition = value;
                timeTv.setText(StringUtil.millsecond2Hour(mPlayer.getCurrentPosition()) + "/" + StringUtil.millsecond2Hour(duration));
                if (isUserControlling) {
                    mPlayer.seekTo(finalPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
                isUserControlling = true;
                playPause();
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                isUserControlling = false;
                if (finalPosition >= 0) {
                    mPlayer.seekTo(finalPosition);
                    playStart();
                    hideControl();
                }
            }
        });
        timeTv = (TextView) findViewById(R.id.time_tv);
        fullScreenIv = (ImageView) findViewById(R.id.full_screen_iv);
        divideV = findViewById(R.id.divide_v);
        int height = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = getResources().getDimensionPixelSize(resourceId);
        }
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, height);
        divideV.setLayoutParams(params);
        playIv = findViewById(R.id.play_iv);
        translateIv = findViewById(R.id.translate_iv);
        backIv = findViewById(R.id.back_iv);
        forwardIv = findViewById(R.id.forward_iv);
        centerPlayIv = findViewById(R.id.center_play_iv);
        playSpeedTv = (TextView) findViewById(R.id.play_speed_tv);
        sppeedTv200 = (TextView) findViewById(R.id.sppeed_tv200);
        sppeedTv175 = (TextView) findViewById(R.id.sppeed_tv175);
        sppeedTv150 = (TextView) findViewById(R.id.sppeed_tv150);
        sppeedTv125 = (TextView) findViewById(R.id.sppeed_tv125);
        sppeedTv100 = (TextView) findViewById(R.id.sppeed_tv100);
        sppeedTv75 = (TextView) findViewById(R.id.sppeed_tv75);
        sppeedTv50 = (TextView) findViewById(R.id.sppeed_tv50);
        speedCl = findViewById(R.id.speed_cl);
        setSpeedClVisible(false);

        playSpeedTv.setOnClickListener(this);
        sppeedTv200.setOnClickListener(this);
        sppeedTv175.setOnClickListener(this);
        sppeedTv150.setOnClickListener(this);
        sppeedTv125.setOnClickListener(this);
        sppeedTv100.setOnClickListener(this);
        sppeedTv75.setOnClickListener(this);
        sppeedTv50.setOnClickListener(this);
        centerPlayIv.setOnClickListener(this);
        backIv.setOnClickListener(this);
        forwardIv.setOnClickListener(this);
        translateIv.setOnClickListener(this);
        playIv.setOnClickListener(this);
        videoSv.setOnClickListener(this);
        fullScreenIv.setOnClickListener(this);
        chooseUriBtn.setOnClickListener(this);
        shelterDv.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
        mPlayer.setDisplay(holder);
        if (!isPrepared) {
            mPlayer.prepareAsync();
        } else if (!mPlayer.isPlaying()) {
            mPlayer.reset();
            try {
                mPlayer.setDataSource(this, Uri.parse(url));
                mPlayer.prepare();
                mPlayer.seekTo(lastPauseLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //为了让暂停不至于黑屏
        mPlayer.seekTo(mPlayer.getCurrentPosition());
    }

    private void saveState() {
        SharedPreferencesUtils.setSharedPreferencesData(this, id + "progress",
                mPlayer.getCurrentPosition());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        baseToast.showToast("code:" + keyCode);
        return super.onKeyDown(keyCode, event);
    }

    private void recoverState() {
        int p = SharedPreferencesUtils.getIntSharedPreferencesData(this,
                id + "progress");
        if (p >= 0) {
            mPlayer.seekTo(p);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        playPause();
        saveState();
        sManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        playStart();
        sManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recoverState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            saveState();
            mPlayer.release();
        }
        db.closeDb();
    }

    private void playStart() {
        if (isPlaying()) {
            return;
        }
        mPlayer.setDisplay(mSurfaceHolder);
        mPlayer.start();
        turnToStartUI();
    }

    private void turnToStartUI() {
        playIv.setImageResource(R.drawable.ic_pause);
        centerPlayIv.setImageResource(R.drawable.ic_pause_white);
        if (!controlGroup.isShown()) {
            centerControlGroup.setVisibility(View.GONE);
        }
        mHandler.removeMessages(RELOCATION_PROGRESS);
        setShelterVisible(true);
    }

    private void playPause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            playIv.setImageResource(R.drawable.ic_play);
            centerPlayIv.setImageResource(R.drawable.ic_play_white);
            setShelterVisible(false);
            centerControlGroup.setVisibility(View.VISIBLE);
            lastPauseLocation = mPlayer.getCurrentPosition();
            if (jumpGap == -1) {
                //用户未设置
                mHandler.sendEmptyMessageDelayed(RELOCATION_PROGRESS, 5000);
            } else if (jumpGap == 0) {
                //用户不需要这个功能
            } else {
                //用户设置了具体值
                mHandler.sendEmptyMessageDelayed(RELOCATION_PROGRESS, jumpGap);
            }
        }
    }

    /**
     * 视频状态
     *
     * @return 视频是否正在播放
     */
    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Logger.d("surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Logger.d("surfaceDestroyed");
        lastPauseLocation = mPlayer.getCurrentPosition();
        mPlayer.stop();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Logger.d("onCompletion");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Logger.d("onError");
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        Logger.d("onInfo");
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
        duration = mPlayer.getDuration();
        progressSb.setMax(duration);
        titleTv.setText(title);
        recoverState();
        Handler handler = new Handler();
        Runnable updateThread = new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (videoWidth > videoHeight) {
                            setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
                        recoverSpeed();
                        playStart();
                    }
                });
            }
        };
        handler.postDelayed(updateThread, 500);

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Logger.d("onSeekComplete");
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Logger.d("onVideoSizeChanged");
        videoWidth = width;
        videoHeight = height;
        resizeSurfaceView();
    }

    private void resizeSurfaceView() {
        resizeSurfaceView(getOrientation());
    }

    private void resizeSurfaceView(int orientation) {
        ViewGroup.LayoutParams lp = videoSv.getLayoutParams();
        ViewGroup.LayoutParams shelterLp = shelterDv.getLayoutParams();
        int finalWidth = 0, finalHeight = 0;
        switch (orientation) {
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                finalWidth = screenWidth;
                finalHeight = (int) ((Double.valueOf(screenWidth) / Double.valueOf(videoWidth)) * videoHeight);
                break;
            default:
                //这个方法是相对于竖屏的 所以width实际是height
                finalWidth = (int) ((Double.valueOf(videoHeight) / Double.valueOf(videoWidth)) * screenHeight);
                //这个方法是自适应的 所以width就是width
                finalHeight = screenWidth;
                break;
        }
        lp.width = finalWidth;
        lp.height = finalHeight;
        shelterLp.width = screenHeight;
        if (shelterHeight == -1) {
            //用户未设置
            shelterLp.height = DisplayUtil.dip2px(this, 30);
        } else if (shelterHeight == 0) {
            //用户不需要这个功能
//            shelterLp.height = 0;
            userBannedShelter = true;
            setShelterVisible(false);
        } else {
            //用户设置了具体值
            shelterLp.height = DisplayUtil.dip2px(this, shelterHeight);
        }
        videoSv.setLayoutParams(lp);
        shelterDv.setLayoutParams(shelterLp);
        mSurfaceHolder.setFixedSize(finalWidth, finalHeight);
    }

    private void setOrientation(int orientation) {
        if (orientation != getOrientation()) {
            setRequestedOrientation(orientation);
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//                baseTopBar.setVisibility(View.VISIBLE);
                titleTv.setVisibility(View.GONE);
                resizeSurfaceView(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                fullScreenIv.setImageResource(R.drawable.ic_full_screen1);
            } else {
//                baseTopBar.setVisibility(View.GONE);
                titleTv.setVisibility(View.VISIBLE);
                shelterDv.toLastPosition();
                resizeSurfaceView(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                fullScreenIv.setImageResource(R.drawable.ic_full_screen_exit1);
            }
        }
    }

    private void setShelterVisible(boolean show) {
        if (show && !userBannedShelter) {
            shelterDv.setVisibility(View.VISIBLE);
        } else {
            shelterDv.setVisibility(View.GONE);
        }
    }

    private void recoverSpeed() {
        String speedS = SharedPreferencesUtils.getSharedPreferencesData(this, ShareKeys.VIDEO_SPEED);
        if (!TextUtils.isEmpty(speedS)) {
            try {
                float speed = Float.valueOf(speedS);
                changeplayerSpeed(speed);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * (1) 使用这个接口可以进行播放速率的设置。
     * (2) 播放器prepared状态之前调用这个方法不会更改播放器的状态。
     * (3) prepared状态之后设置速率0等同于调用pause()，当调用start恢复播放以后，将以原来的速率进行播放。
     * (4) prepared状态之后设置非0的速率等同于调用start()。
     * (5) 当播放器还未初始化或者已经被释放的时候设置会抛IllegalStateException的异常。
     * (6) 当参数不支持的时候会抛IllegalArgumentException的异常。
     *
     * @param speed
     */
    private void changeplayerSpeed(float speed) {
        // this checks on API 23 and up
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mPlayer.isPlaying()) {
                mPlayer.setPlaybackParams(mPlayer.getPlaybackParams().setSpeed(speed));
            } else {
                mPlayer.setPlaybackParams(mPlayer.getPlaybackParams().setSpeed(speed));
                mPlayer.pause();
            }
            setSpeedClVisible(false);
            sppeedTv50.setTextColor(getResources().getColor(R.color.white));
            sppeedTv75.setTextColor(getResources().getColor(R.color.white));
            sppeedTv100.setTextColor(getResources().getColor(R.color.white));
            sppeedTv125.setTextColor(getResources().getColor(R.color.white));
            sppeedTv175.setTextColor(getResources().getColor(R.color.white));
            sppeedTv200.setTextColor(getResources().getColor(R.color.white));
            sppeedTv150.setTextColor(getResources().getColor(R.color.white));
            if (Float.compare(speed, 0.5f) == 0) {
                sppeedTv50.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else if (Float.compare(speed, 0.75f) == 0) {
                sppeedTv75.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else if (Float.compare(speed, 1f) == 0) {
                sppeedTv100.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else if (Float.compare(speed, 1.25f) == 0) {
                sppeedTv125.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else if (Float.compare(speed, 1.5f) == 0) {
                sppeedTv150.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else if (Float.compare(speed, 1.75f) == 0) {
                sppeedTv175.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else if (Float.compare(speed, 2f) == 0) {
                sppeedTv200.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
//            turnToStartUI();
            SharedPreferencesUtils.setSharedPreferencesData(this, ShareKeys.VIDEO_SPEED, speed + "");
        }
    }

    @Override
    public void onBackPressed() {
//        if (isPortrait()) {
        super.onBackPressed();
//        } else {
//            setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
    }

    private boolean isPortrait() {
        return getOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    // 判断当前屏幕朝向是否为竖屏
    private int getOrientation() {
        return getApplicationContext().getResources().getConfiguration().orientation;
    }

    private void showControl() {
        controlGroup.setVisibility(View.VISIBLE);
        centerControlGroup.setVisibility(View.VISIBLE);
        mHandler.removeMessages(HIDE_CONTROL);
        mHandler.sendEmptyMessage(UPDATE_TIME);
        mHandler.sendEmptyMessageDelayed(HIDE_CONTROL, 3000);
    }

    private void hideControl() {

        mHandler.removeMessages(UPDATE_TIME);
        controlGroup.setVisibility(View.GONE);
        setSpeedClVisible(false);
        try {
            if (null != mPlayer && mPlayer.isPlaying()) {
                centerControlGroup.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void translateWord(final String word, final Bitmap bp) {
//        clip.setText(word);
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData(this, ShareKeys.CLOSE_TRANSLATE, false)) {
            //关闭自动翻译
            return;
        }
        String url = Configure.YOUDAO + word;
        HashMap<String, String> params = new HashMap<String, String>();
        VolleyCallBack<YoudaoResponse> callback = new VolleyCallBack<YoudaoResponse>() {

            @Override
            public void loadSucceed(YoudaoResponse result) {
                if (null != result && result.getErrorCode() == 0) {
                    YoudaoResponse.BasicBean item = result.getBasic();
                    String t = "";
                    if (null != item) {
                        for (int i = 0; i < item.getExplains().size(); i++) {
                            t = t + item.getExplains().get(i) + ";";
                        }
                        //记录查过的单词 本地翻译关不了 完全可以把释义也加进去
                        db.insertWordsBookTb(VideoActivity.this, word, "[" + item.getPhonetic() + "]: " + t, bp);
                        showTranslateResultDialog(word, result.getQuery() + "  [" + item.getPhonetic() + "]: " + "\n" + t);
                    } else {
                        baseToast.showToast("没查到该词");
                    }
                } else {
                    baseToast.showToast("没查到该词");
                }
            }

            @Override
            public void loadFailed(VolleyError error) {
                baseToast.showToast("error\n" + error);
            }

            @Override
            public void loadSucceedButNotNormal(YoudaoResponse result) {

            }
        };
        VolleyTool.getInstance(this).requestData(Request.Method.GET,
                this, url, params,
                YoudaoResponse.class, callback);
    }

    private void showTranslateResultDialog(final String title, String msg) {
        if (null == translateResultDialog) {
            translateResultDialog = new TranslateDialog(this);
            translateResultDialog.setOnPeanutDialogClickListener(new TranslateDialog.OnPeanutDialogClickListener() {
                @Override
                public void onOkClick() {
                }

                @Override
                public void onCancelClick() {
                }
            });
        }
        translateResultDialog.show();

        translateResultDialog.setTitle(title);
        translateResultDialog.setMessage(msg);
        translateResultDialog.setOkText("确定");
        translateResultDialog.setCancelable(true);
    }

    private void showImgLandscapeKeyBoardDialog(final Bitmap bp) {
        ImgLandsacpeKeyboardDialog dialog = new ImgLandsacpeKeyboardDialog(this);
        dialog.setKeyBorad26Listener(new English26KeyBoardView.KeyBorad26Listener() {
            @Override
            public void inputFinish(String s) {
                translateWord(s, bp);
            }

            @Override
            public void closeKeyboard() {

            }
        });
        dialog.show();
        dialog.setImgRes(bp);
    }

    private void setSpeedClVisible(boolean visible) {
        if (visible) {
            speedCl.setVisibility(View.VISIBLE);
        } else {
            speedCl.setVisibility(View.GONE);
        }
    }

    private void showImgEditDialog(final Bitmap bp) {
        MangaImgEditDialog mangaImgEditDialog = new MangaImgEditDialog(this);
        mangaImgEditDialog.setOnEditResultListener(new OnEditResultListener() {
            @Override
            public void onResult(String text) {
                translateWord(text, bp);
            }

            @Override
            public void onCancelClick() {

            }
        });
        mangaImgEditDialog.show();
        mangaImgEditDialog.setImgRes(bp);
        mangaImgEditDialog.clearEdit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_speed_tv:
                setSpeedClVisible(!speedCl.isShown());
                break;
            case R.id.sppeed_tv50:
                changeplayerSpeed(0.5f);
                break;
            case R.id.sppeed_tv75:
                changeplayerSpeed(0.75f);
                break;
            case R.id.sppeed_tv100:
                changeplayerSpeed(1f);
                break;
            case R.id.sppeed_tv125:
                changeplayerSpeed(1.25f);
                break;
            case R.id.sppeed_tv150:
                changeplayerSpeed(1.5f);
                break;
            case R.id.sppeed_tv175:
                changeplayerSpeed(1.75f);
                break;
            case R.id.sppeed_tv200:
                changeplayerSpeed(2f);
                break;
            case R.id.choose_uri_btn:

                break;
            case R.id.full_screen_iv:
                if (isPortrait()) {
                    setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
            case R.id.play_iv:
            case R.id.shelter_dv:
            case R.id.center_play_iv:
                if (mPlayer.isPlaying()) {
                    playPause();
                } else {
                    playStart();
                }
                break;
            case R.id.video_sv:
                if (controlGroup.isShown()) {
                    hideControl();
                } else {
                    showControl();
                }
                tryDoubleClick++;
                doubleClickHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tryDoubleClick = 0;
                    }
                }, 300);
                if (tryDoubleClick >= 2) {
                    if (mPlayer.isPlaying()) {
                        playPause();
                    } else {
                        playStart();
                        hideControl();
                    }
                }
                break;
            case R.id.translate_iv:
                if (FastClickUtil.isNotFastClick()) {
                    playPause();
                    int top = shelterDv.getBottom();
                    double ratio = Double.valueOf(top) / Double.valueOf(screenWidth);
                    //这个方法获取的图片大小是视频的大小 而不是播放控件的大小
                    Bitmap bgBitmap = VideoUtil.getVideoThumbnail(VideoActivity.this, Uri.parse(url), mPlayer.getCurrentPosition());
                    //所以需要按比例换算
                    top = (int) (ratio * bgBitmap.getHeight());
                    Bitmap finalBp = Bitmap.createBitmap(bgBitmap, 0, top, bgBitmap.getWidth(), bgBitmap.getHeight() - top);
                    bgBitmap.recycle();
                    if (DisplayUtil.isPad(VideoActivity.this)) {
                        showImgEditDialog(finalBp);
                    } else {
                        showImgLandscapeKeyBoardDialog(finalBp);
                    }
//                showSearchDialog();
                }
                break;
            case R.id.back_iv:
                mHandler.removeMessages(RELOCATION_PROGRESS);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mPlayer.seekTo(mPlayer.getCurrentPosition() - 5000, MediaPlayer.SEEK_CLOSEST);
                } else {
                    mPlayer.seekTo(mPlayer.getCurrentPosition() - 5000);
                }
                break;
            case R.id.forward_iv:
                mHandler.removeMessages(RELOCATION_PROGRESS);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mPlayer.seekTo(mPlayer.getCurrentPosition() + 5000, MediaPlayer.SEEK_CLOSEST);
                } else {
                    mPlayer.seekTo(mPlayer.getCurrentPosition() + 5000);
                }
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (isPortrait()) {
                return;
            }
            try {
                float gyroscope_x = event.values[0];

//                readProgressTv.setText(gyroscope_x + "\n" + gyroscope_y + "\n" + gyroscope_z);
                if (gyroscope_x >= 8 && Configure.currentOrientation != 90) {
                    Configure.currentOrientation = 90;
                    setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else if (gyroscope_x <= -8 && Configure.currentOrientation != 270) {
                    Configure.currentOrientation = 270;
                    setOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
