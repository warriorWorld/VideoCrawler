package com.insightsurfface.videocrawler.business.filechoose;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;

import com.insightsurface.lib.base.BaseRefreshListActivity;
import com.insightsurface.lib.utils.Logger;
import com.insightsurface.lib.widget.bar.TopBar;
import com.insightsurfface.videocrawler.R;
import com.insightsurfface.videocrawler.adapter.VideoChooserAdapter;
import com.insightsurfface.videocrawler.bean.VideoBean;
import com.insightsurfface.videocrawler.utils.DisplayUtil;
import com.insightsurfface.videocrawler.utils.FileUtils;
import com.insightsurfface.videocrawler.widget.recycler.RecyclerGridDecoration;

import java.util.ArrayList;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class FileChooseActivity extends BaseRefreshListActivity implements View.OnClickListener {
    private VideoChooserAdapter mAdapter;
    private ArrayList<VideoBean> videoList = new ArrayList<>();
    private Button doneBtn;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_btn:
                ArrayList<VideoBean> result = mAdapter.getSelectedList();
                for (VideoBean item : result) {
                    //intent传值有大小限制 而且这个值本来在这里也没用
                    item.setThumbnail(null);
                }
                Intent intent = new Intent();
                intent.putExtra("selectedList", result);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    public enum SelectAllState {
        SELECT_ALL,
        CANCEL_ALL
    }

    private SelectAllState mSelectAllState = SelectAllState.CANCEL_ALL;

    @Override
    protected void onCreateInit() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_file_chooser;
    }

    @Override
    protected void initUI() {
        super.initUI();
        doneBtn = findViewById(R.id.ok_btn);
        refreshRcv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        refreshRcv.setFocusableInTouchMode(false);
        refreshRcv.setFocusable(false);
        refreshRcv.setHasFixedSize(true);
        baseTopBar.setTitle("选择视频");
        baseTopBar.setRightText("全选");
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                switch (mSelectAllState) {
                    case CANCEL_ALL:
                        mAdapter.selectAll();
                        mSelectAllState = SelectAllState.SELECT_ALL;
                        baseTopBar.setRightText("取消全选");
                        break;
                    case SELECT_ALL:
                        mAdapter.removeAllSelected();
                        mSelectAllState = SelectAllState.CANCEL_ALL;
                        baseTopBar.setRightText("全选");
                        break;
                }
            }

            @Override
            public void onTitleClick() {

            }
        });
        doneBtn.setOnClickListener(this);
    }

    @Override
    protected void doGetData() {
        videoList = FileUtils.getVideos(this);
        initRec();
    }

    @Override
    protected void initRec() {
        try {
            if (null == mAdapter) {
                mAdapter = new VideoChooserAdapter(this);
                mAdapter.setList(videoList);
                refreshRcv.setAdapter(mAdapter);
                ColorDrawable dividerDrawable = new ColorDrawable(0x00000000) {
                    @Override
                    public int getIntrinsicHeight() {
                        return DisplayUtil.dip2px(FileChooseActivity.this, 8);
                    }

                    @Override
                    public int getIntrinsicWidth() {
                        return DisplayUtil.dip2px(FileChooseActivity.this, 8);
                    }
                };
                RecyclerGridDecoration itemDecoration = new RecyclerGridDecoration(this,
                        dividerDrawable, true);
                refreshRcv.addItemDecoration(itemDecoration);
            } else {
                mAdapter.setList(videoList);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        swipeToLoadLayout.setRefreshing(false);
    }
}
