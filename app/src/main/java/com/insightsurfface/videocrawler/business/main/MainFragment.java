package com.insightsurfface.videocrawler.business.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.insightsurface.lib.base.BaseRefreshListFragment;
import com.insightsurface.lib.listener.OnDialogClickListener;
import com.insightsurface.lib.listener.OnRecycleItemClickListener;
import com.insightsurface.lib.listener.OnRecycleItemLongClickListener;
import com.insightsurface.lib.utils.ShareObjUtil;
import com.insightsurface.lib.utils.SingleLoadBarUtil;
import com.insightsurface.lib.widget.bar.TopBar;
import com.insightsurface.lib.widget.dialog.NormalDialog;
import com.insightsurfface.videocrawler.R;
import com.insightsurfface.videocrawler.adapter.VideoAdapter;
import com.insightsurfface.videocrawler.bean.VideoBean;
import com.insightsurfface.videocrawler.business.video.TestActivity;
import com.insightsurfface.videocrawler.business.video.VideoActivity;
import com.insightsurfface.videocrawler.db.DbAdapter;
import com.insightsurfface.videocrawler.utils.StringUtil;
import com.insightsurfface.videocrawler.utils.VideoUtil;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class MainFragment extends BaseRefreshListFragment {
    private ArrayList<VideoBean> videoList;
    private VideoAdapter mAdapter;
    private TopBar mTopBar;
    private DbAdapter db;//数据库
    private final int UPDATE_LIST = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_LIST:
                    initRec();
                    break;
            }
        }
    };

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
                Intent intent = new Intent(getActivity(), TestActivity.class);
                startActivity(intent);
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
        intent.setType("video/*");//设置类型和后缀  全部文件
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    public void addVideo(final String path, final int duration) {
        SingleLoadBarUtil.getInstance().showLoadBar(getActivity());
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.insertVideoTableTb(getActivity(), path, StringUtil.cutString(path, '/', '.'), duration, 0);
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                mHandler.sendEmptyMessage(UPDATE_LIST);
            }
        }).start();
    }

    public void deleteVideo(Context context, int id) {
        db.deleteVideoById(context, id);
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
        videoList = db.queryAllVideos(getActivity());

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

    private void showDeleteDialog(final int id) {
        NormalDialog dialog = new NormalDialog(getActivity());
        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onOkClick() {
                deleteVideo(getActivity(), id);
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
                        VideoActivity.startActivity(getActivity(), videoList.get(position).getId(), videoList.get(position).getPath(), videoList.get(position).getTitle());
                    }
                });
                mAdapter.setOnRecycleItemLongClickListener(new OnRecycleItemLongClickListener() {
                    @Override
                    public void onItemLongClick(int position) {
                        showDeleteDialog(videoList.get(position).getId());
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
