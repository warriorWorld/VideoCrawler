package com.insightsurfface.videocrawler.business.words;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.ClipboardManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.insightsurface.lib.base.BaseRefreshListActivity;
import com.insightsurface.lib.listener.OnRecycleItemClickListener;
import com.insightsurface.lib.listener.OnRecycleItemLongClickListener;
import com.insightsurface.lib.utils.VibratorUtil;
import com.insightsurfface.videocrawler.R;
import com.insightsurfface.videocrawler.adapter.WordsAdapter;
import com.insightsurfface.videocrawler.bean.WordsBookBean;
import com.insightsurfface.videocrawler.config.Configure;
import com.insightsurfface.videocrawler.db.DbAdapter;

import java.util.ArrayList;

public class WordsActivity extends BaseRefreshListActivity implements SensorEventListener {
    private ArrayList<WordsBookBean> wordsList = new ArrayList<WordsBookBean>();
    private DbAdapter db;//数据库
    private WordsAdapter mAdapter;
    private RelativeLayout topBar;
    private TextView topBarLeft;
    private TextView topBarRight;
    private ClipboardManager clip;//复制文本用
    private SensorManager sManager;
    private Sensor mSensorAccelerometer;

    @Override
    protected void onCreateInit() {
        db = new DbAdapter(this);
        clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        initSensorManager();
    }

    private void initSensorManager() {
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorAccelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_words;
    }

    @Override
    protected void initUI() {
        super.initUI();
        topBar = (RelativeLayout) findViewById(R.id.top_bar);
        topBarLeft = (TextView) findViewById(R.id.top_bar_left);
        topBarRight = (TextView) findViewById(R.id.top_bar_right);
        baseTopBar.setTitle("生词本");
        hideBaseTopBar();
    }

    @Override
    protected void doGetData() {
        wordsList = db.queryAllWordsBook(this);
        initRec();
    }

    @Override
    protected void initRec() {
        topBarLeft.setText("总计：" + wordsList.size() + "个生词");
        try {
            if (null == mAdapter) {
                mAdapter = new WordsAdapter(this);
                mAdapter.setList(wordsList);
                mAdapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        VibratorUtil.Vibrate(WordsActivity.this, 60);
                        db.deleteWordByWord(WordsActivity.this, wordsList.get(position).getWord());
                        doGetData();
                    }
                });
                mAdapter.setOnRecycleItemLongClickListener(new OnRecycleItemLongClickListener() {
                    @Override
                    public void onItemLongClick(int position) {
                        clip.setText(wordsList.get(position).getWord());
                    }
                });
                refreshRcv.setAdapter(mAdapter);
            } else {
                mAdapter.setList(wordsList);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            noMoreData();
        }
        noMoreData();
    }

    private boolean isPortrait() {
        return getOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    private void setOrientation(int orientation) {
        setRequestedOrientation(orientation);
    }

    // 判断当前屏幕朝向是否为竖屏
    private int getOrientation() {
        return getApplicationContext().getResources().getConfiguration().orientation;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.closeDb();
    }
}
