package com.insightsurfface.videocrawler.business.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.insightsurface.lib.base.BaseRefreshListFragment;
import com.insightsurface.lib.listener.OnDialogClickListener;
import com.insightsurface.lib.listener.OnRecycleItemClickListener;
import com.insightsurface.lib.listener.OnRecycleItemLongClickListener;
import com.insightsurface.lib.widget.bar.TopBar;
import com.insightsurface.lib.widget.dialog.NormalDialog;
import com.insightsurfface.videocrawler.R;
import com.insightsurfface.videocrawler.adapter.VideoAdapter;
import com.insightsurfface.videocrawler.business.bean.VideoBean;
import com.insightsurfface.videocrawler.business.video.VideoActivity;
import com.insightsurfface.videocrawler.db.DbAdapter;
import com.insightsurfface.videocrawler.utils.StringUtil;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class MainFragment extends BaseRefreshListFragment {
    private ArrayList<VideoBean> videoList;
    private VideoAdapter mAdapter;
    private TopBar mTopBar;
    private DbAdapter db;//数据库

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DbAdapter(getActivity());
    }

    @Override
    protected void initUI(View v) {
        super.initUI(v);
        mTopBar = v.findViewById(R.id.gradient_bar);
        mTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {

            }

            @Override
            public void onRightClick() {
                showFileChooser();
            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    /**
     * 调用文件选择软件来选择文件
     **/
    public void showFileChooser() {
        baseToast.showToast("选择视频文件（目前支持3gp,mp4,avi）");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("text/plain");//设置类型和后缀 txt
        intent.setType("*/*");//设置类型和后缀  全部文件
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    public void addVideo(String path, int duration) {
        db.insertVideoTableTb(path, StringUtil.cutString(path, '/', '.'), duration, 0);
        doGetData();
    }

    public void deleteBooks(String path) {
        db.deleteVideoByPath(path);
        doGetData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            try {
                doGetData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doGetData() {
        videoList = db.queryAllVideos();

        initRec();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    public void scrollToTop() {
        refreshRcv.scrollToPosition(0);
    }

    @Override
    protected String getType() {
        return null;
    }

    private void showDeleteDialog(final String id) {
        NormalDialog dialog = new NormalDialog(getActivity());
        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onOkClick() {
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setTitle("是否删除该视频?");
        dialog.setOkBtnText("删除");
        dialog.setCancelBtnText("取消");
    }

    @Override
    protected void initRec() {
        try {
            if (null == mAdapter) {
                mAdapter = new VideoAdapter(getActivity());
                mAdapter.setList(videoList);
                mAdapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        VideoActivity.startActivity(getActivity(), videoList.get(position).getId(),videoList.get(position).getPath(),videoList.get(position).getTitle());
                    }
                });
                mAdapter.setOnRecycleItemLongClickListener(new OnRecycleItemLongClickListener() {
                    @Override
                    public void onItemLongClick(int position) {
                        showDeleteDialog(videoList.get(position).getPath());
                    }
                });
                refreshRcv.setAdapter(mAdapter);
            } else {
                mAdapter.setList(videoList);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            noMoreData();
        }
        noMoreData();
    }
}
