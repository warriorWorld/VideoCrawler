package com.insightsurfface.videocrawler.business.video;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.insightsurfface.videocrawler.R;
import com.insightsurfface.videocrawler.base.BaseActivity;

public class VideoActivity extends BaseActivity implements SurfaceHolder.Callback,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnVideoSizeChangedListener,
        View.OnClickListener {
    //    private VideoView crawlerVv;
    private SurfaceView videoSv;
    private String testURL = "http://ugcws.video.gtimg.com/uwMROfz2r5zAoaQXGdGnC2dfJ6nM3DInWQqp2axRinGnB-kO/r3043xtjgug.p701.1.mp4?sdtfrom=v1104&guid=4c68826f6ff46a643a05b409826286dd&vkey=E16F961FF0E960562170443D6717CE8CA5CF7216E939E0E7B3B330999D3596F7ED1AAFD38A785573A0D768BAD5814B0FC39C816DDC8A3010586FDAD399A7537BF9969F79C2051082E8A275C66CBB92E419CA5D99C5504123D86E2FA67FF34F618A8448C1888D8A048F4AB211E112A407E23051D7BE2367CB6059549099F86C23";
    private Button chooseUriBtn;
    private MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSurfaceView();
        initPlayer();
    }

    private void initSurfaceView() {
        videoSv.setZOrderOnTop(false);
        videoSv.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        videoSv.getHolder().addCallback(this);
    }

    private void initPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnInfoListener(this);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnSeekCompleteListener(this);
        mPlayer.setOnVideoSizeChangedListener(this);
        try {
            //使用手机本地视频
            mPlayer.setDataSource(this, Uri.parse(testURL));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initUI() {
        super.initUI();
        videoSv = findViewById(R.id.video_sv);
        chooseUriBtn = findViewById(R.id.choose_uri_btn);
        chooseUriBtn.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mPlayer.setDisplay(holder);
        mPlayer.prepareAsync();
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

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.choose_uri_btn:
                mPlayer.start();
                break;
        }
    }
}
