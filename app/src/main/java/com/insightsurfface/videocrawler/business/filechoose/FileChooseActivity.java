package com.insightsurfface.videocrawler.business.filechoose;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;

import com.insightsurface.lib.base.BaseRefreshActivity;
import com.insightsurface.lib.base.BaseRefreshListActivity;
import com.insightsurface.lib.listener.OnRecycleItemClickListener;
import com.insightsurface.lib.widget.bar.TopBar;
import com.insightsurfface.videocrawler.adapter.VideoChooserAdapter;
import com.insightsurfface.videocrawler.base.BaseActivity;
import com.insightsurfface.videocrawler.bean.VideoBean;
import com.insightsurfface.videocrawler.utils.DisplayUtil;
import com.insightsurfface.videocrawler.utils.FileUtils;
import com.insightsurfface.videocrawler.widget.recycler.RecyclerGridDecoration;

import java.util.ArrayList;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class FileChooseActivity extends BaseRefreshListActivity {
    private VideoChooserAdapter mAdapter;
    private ArrayList<VideoBean> videoList = new ArrayList<>();

    @Override
    protected void onCreateInit() {

    }

    @Override
    protected void initUI() {
        super.initUI();
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

            }

            @Override
            public void onTitleClick() {

            }
        });
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
                mAdapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                    }
                });
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
        }
        swipeToLoadLayout.setRefreshing(false);
    }
}
