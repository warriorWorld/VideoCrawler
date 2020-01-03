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
import android.os.Message;
import android.text.ClipboardManager;
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
import com.insightsurfface.videocrawler.R;
import com.insightsurfface.videocrawler.base.BaseActivity;
import com.insightsurfface.videocrawler.bean.YoudaoResponse;
import com.insightsurfface.videocrawler.config.Configure;
import com.insightsurfface.videocrawler.config.ShareKeys;
import com.insightsurfface.videocrawler.listener.OnEditResultListener;
import com.insightsurfface.videocrawler.utils.DisplayUtil;
import com.insightsurfface.videocrawler.utils.ScreenShot;
import com.insightsurfface.videocrawler.utils.StringUtil;
import com.insightsurfface.videocrawler.volley.VolleyCallBack;
import com.insightsurfface.videocrawler.volley.VolleyTool;
import com.insightsurfface.videocrawler.widget.dialog.MangaImgEditDialog;
import com.insightsurfface.videocrawler.widget.dialog.OnlyEditDialog;
import com.insightsurfface.videocrawler.widget.dialog.TranslateDialog;
import com.insightsurfface.videocrawler.widget.dragview.ShelterView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.HashMap;

import androidx.annotation.NonNull;

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
    private SurfaceView videoSv;
    private String testURL = "http://ugcws.video.gtimg.com/uwMROfz2r5zAoaQXGdGnC2dfJ6nM3DInWQqp2axRinGnB-kO/r3043xtjgug.p701.1.mp4?sdtfrom=v1104&guid=4c68826f6ff46a643a05b409826286dd&vkey=E16F961FF0E960562170443D6717CE8CA5CF7216E939E0E7B3B330999D3596F7ED1AAFD38A785573A0D768BAD5814B0FC39C816DDC8A3010586FDAD399A7537BF9969F79C2051082E8A275C66CBB92E419CA5D99C5504123D86E2FA67FF34F618A8448C1888D8A048F4AB211E112A407E23051D7BE2367CB6059549099F86C23";
    private Button chooseUriBtn;
    private MediaPlayer mPlayer;
    private String url;
    private SurfaceHolder mSurfaceHolder;
    private int videoWidth = 0, videoHeight = 0;
    private int screenWidth = 0, screenHeight = 0;
    private RelativeLayout controlRl;
    private TextView titleTv;
    private DiscreteSeekBar progressSb;
    private TextView timeTv;
    private ImageView fullScreenIv;
    private View divideV;
    public static final int UPDATE_TIME = 0x0001;
    public static final int HIDE_CONTROL = 0x0002;
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
            }
        }
    };
    private ImageView playIv;
    private String title;
    private int duration = 0;
    private int finalPosition = 0;
    private boolean userControling = false;
    private int id;
    private ShelterView shelterDv;
    private ClipboardManager clip;//复制文本用
    private TranslateDialog translateResultDialog;
    private OnlyEditDialog searchDialog;
    private ImageView translateIv;
    private SensorManager sManager;
    private Sensor mSensorAccelerometer;
    private ImageView backIv, forwardIv, centerPlayIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mPlayer.setOnSubtitleDataListener(new MediaPlayer.OnSubtitleDataListener() {
                @Override
                public void onSubtitleData(@NonNull MediaPlayer mp, @NonNull SubtitleData data) {
                    Logger.d(data.toString());
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
        try {
            int currentSecond = (int) ((mPlayer.getCurrentPosition() / 1000f));
            timeTv.setText(StringUtil.second2Hour(currentSecond) + "/" + StringUtil.second2Hour(duration));
            progressSb.setProgress(currentSecond);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initUI() {
        super.initUI();
        videoSv = findViewById(R.id.video_sv);
        chooseUriBtn = findViewById(R.id.choose_uri_btn);
        controlRl = (RelativeLayout) findViewById(R.id.control_rl);
        controlRl.setVisibility(View.GONE);
        shelterDv = findViewById(R.id.shelter_dv);
        shelterDv.setSavePosition(true);
        shelterDv.setLastPosKey("POS_KEY" + id);
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
                if (userControling) {
                    mPlayer.seekTo(finalPosition * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
                playPause();
                userControling = true;
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                if (finalPosition >= 0) {
//                    mPlayer.seekTo(finalPosition * 1000);
                    userControling = false;
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
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        divideV.setLayoutParams(params);
        playIv = findViewById(R.id.play_iv);
        translateIv = findViewById(R.id.translate_iv);
        backIv = findViewById(R.id.back_iv);
        forwardIv = findViewById(R.id.forward_iv);
        centerPlayIv = findViewById(R.id.center_play_iv);

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
        mPlayer.prepareAsync();
        //为了让暂停不至于黑屏
        mPlayer.seekTo(mPlayer.getCurrentPosition());
    }

    private void saveState() {
        SharedPreferencesUtils.setSharedPreferencesData(this, id + "progress",
                mPlayer.getCurrentPosition());
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
    }

    private void playStart() {
        if (isPlaying()) {
            return;
        }
        mPlayer.setDisplay(mSurfaceHolder);
        mPlayer.start();
        playIv.setImageResource(R.drawable.ic_pause);
        centerPlayIv.setImageResource(R.drawable.ic_pause_white);
    }

    private void playPause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            playIv.setImageResource(R.drawable.ic_play);
            centerPlayIv.setImageResource(R.drawable.ic_play_white);
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

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        duration = (int) (mPlayer.getDuration() / 1000f);
        progressSb.setMax(duration);
        titleTv.setText(title);
        recoverState();
        playStart();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

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
        ViewGroup.LayoutParams controlLp = controlRl.getLayoutParams();
        ViewGroup.LayoutParams shelterLp = shelterDv.getLayoutParams();
        int finalWidth = 0, finalHeight = 0;
        switch (orientation) {
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                finalWidth = screenWidth;
                finalHeight = (int) ((Double.valueOf(screenWidth) / Double.valueOf(videoWidth)) * videoHeight);
                break;
            default:
                //这个方法是相对于竖屏的 所以width实际是height
                finalWidth = screenHeight;
                //这个方法是自适应的 所以width就是width
                finalHeight = screenWidth;
                break;
        }
        lp.width = finalWidth;
        lp.height = finalHeight;
        controlLp.width = finalWidth;
        controlLp.height = finalHeight;
        shelterLp.width = finalWidth;
        shelterLp.height = DisplayUtil.dip2px(this, 30);
        videoSv.setLayoutParams(lp);
        controlRl.setLayoutParams(controlLp);
        shelterDv.setLayoutParams(shelterLp);
        mSurfaceHolder.setFixedSize(finalWidth, finalHeight);
    }

    private void setOrientation(int orientation) {
        if (orientation != getOrientation()) {
            setRequestedOrientation(orientation);
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//                baseTopBar.setVisibility(View.VISIBLE);
                titleTv.setVisibility(View.GONE);
                shelterDv.setVisibility(View.GONE);
                chooseUriBtn.setVisibility(View.VISIBLE);
                resizeSurfaceView(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                fullScreenIv.setImageResource(R.drawable.ic_full_screen1);
            } else {
//                baseTopBar.setVisibility(View.GONE);
                titleTv.setVisibility(View.VISIBLE);
                chooseUriBtn.setVisibility(View.GONE);
                shelterDv.toLastPosition();
                resizeSurfaceView(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                fullScreenIv.setImageResource(R.drawable.ic_full_screen_exit1);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isPortrait()) {
            super.onBackPressed();
        } else {
            setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private boolean isPortrait() {
        return getOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    // 判断当前屏幕朝向是否为竖屏
    private int getOrientation() {
        return getApplicationContext().getResources().getConfiguration().orientation;
    }

    private void showControl() {
        controlRl.setVisibility(View.VISIBLE);
        mHandler.removeMessages(HIDE_CONTROL);
        mHandler.sendEmptyMessage(UPDATE_TIME);
        mHandler.sendEmptyMessageDelayed(HIDE_CONTROL, 10000);
        shelterDv.setVisibility(View.GONE);
    }

    private void hideControl() {
        if (userControling) {
            return;
        }
        mHandler.removeMessages(UPDATE_TIME);
        controlRl.setVisibility(View.GONE);
        if (!isPortrait()) {
            shelterDv.setVisibility(View.VISIBLE);
        }
    }

    private void translateWord(final String word) {
//        clip.setText(word);
        //记录查过的单词
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
                    playStart();
                }

                @Override
                public void onCancelClick() {
                    playStart();
                }
            });
        }
        translateResultDialog.show();

        translateResultDialog.setTitle(title);
        translateResultDialog.setMessage(msg);
        translateResultDialog.setOkText("确定");
        translateResultDialog.setCancelable(true);
    }

    private void showSearchDialog() {
        playPause();
        if (null == searchDialog) {
            searchDialog = new OnlyEditDialog(this);
            searchDialog.setOnEditResultListener(new OnEditResultListener() {
                @Override
                public void onResult(String text) {
                    translateWord(text);
                }

                @Override
                public void onCancelClick() {

                }
            });
            searchDialog.setCancelable(true);
        }
        searchDialog.show();
        searchDialog.clearEdit();
    }

    private void showImgLandscapeKeyBoardDialog(Bitmap bp) {
        MangaImgEditDialog dialog = new MangaImgEditDialog(this);
        dialog.setOnEditResultListener(new OnEditResultListener() {
            @Override
            public void onResult(String text) {
                translateWord(text);
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setImgRes(bp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.full_screen_iv:
                if (isPortrait()) {
                    setOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                } else {
                    setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
            case R.id.play_iv:
                if (mPlayer.isPlaying()) {
                    playPause();
                    showControl();
                } else {
                    playStart();
                    hideControl();
                }
                break;
            case R.id.shelter_dv:
            case R.id.center_play_iv:
                if (mPlayer.isPlaying()) {
                    playPause();
                } else {
                    playStart();
                }
                break;
            case R.id.video_sv:
                if (controlRl.isShown()) {
                    hideControl();
                } else {
                    showControl();
                }
                break;
            case R.id.translate_iv:
//                int top=shelterDv.getBottom();
//                Bitmap bgBitmap = ScreenShot.takeScreenShot(this, 0,screenHeight,top, screenWidth-top);
//                showImgLandscapeKeyBoardDialog(bgBitmap);
                showSearchDialog();
                break;
            case R.id.back_iv:
                mPlayer.seekTo(mPlayer.getCurrentPosition() - 5000);
                if (!mPlayer.isPlaying()) {
                    playPause();
                }
                break;
            case R.id.forward_iv:
                mPlayer.seekTo(mPlayer.getCurrentPosition() + 5000);
                if (!mPlayer.isPlaying()) {
                    playPause();
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
