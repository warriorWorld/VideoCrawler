package com.insightsurfface.videocrawler.business.video;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;

import com.insightsurfface.videocrawler.R;
import com.insightsurfface.videocrawler.base.BaseActivity;

public class TestActivity extends BaseActivity implements View.OnClickListener, SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnVideoSizeChangedListener, SeekBar.OnSeekBarChangeListener {
    private String testURL = "http://ugccsy.qq.com/uwMROfz2r5zBIaQXGdGnC2dfJ6nAzEoO290Tsw9-2jpi_xWl/e3045nq422n.p701.1.mp4?sdtfrom=v1104&guid=4c68826f6ff46a643a05b409826286dd&vkey=B71428DD4372FA1C4D17F2139A5C470AF29469EBA261C6D167C28F10BBCF665B25C3F1AB1FC5C0C30A3E38E4ABE89ABE289D1DA018BBBE911B4925AD383DC3DB65796F0637199E68D8617FCFDA5BBDC19875A49FBC7912578A28DC8C441BDC47B7E48DC5B585051F43E13B2C0B63254E4DA1F356D92587BE2C16608CFDAB21EC";
    private SurfaceView playerSfv;
    private View playView;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder playerHolder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerSfv = findViewById(R.id.video_sv);
        playView = findViewById(R.id.center_play_iv);
        playView.setOnClickListener(this);
        setupPlayer();
    }

    private void setupPlayer() {
        mediaPlayer = new MediaPlayer();
        playerHolder = playerSfv.getHolder();
        playerHolder.addCallback(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                    String testUrl = "http://v.jxvdy.com/sendfile/w5bgP3A8JgiQQo5l0hvoNGE2H16WbN09X-ONHPq3P3C1BISgf7C-qVs6_c8oaw3zKScO78I--b0BGFBRxlpw13sf2e54QA";
//                    mediaPlayer.setDataSource(CookbookDetailActivity.this, Uri.parse(testUrl));
                    mediaPlayer.setDataSource(TestActivity.this, Uri.parse(testURL));
                    mediaPlayer.setOnVideoSizeChangedListener(TestActivity.this);
                    mediaPlayer.setDisplay(playerHolder);
                    mediaPlayer.prepare();
                    mediaPlayer.setOnPreparedListener(TestActivity.this);
                } catch (Exception e) {
                }
            }
        }).start();
    }

    private void play() {
        mediaPlayer.start();
    }


    private void pause() {
        mediaPlayer.pause();
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.center_play_iv:
                if (mediaPlayer.isPlaying()) {
                    pause();
                } else {
                    play();
                }
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
