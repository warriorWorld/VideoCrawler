package com.insightsurfface.videocrawler.business.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

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
import com.insightsurfface.videocrawler.business.filechoose.FileChooseActivity;
import com.insightsurfface.videocrawler.business.video.TestActivity;
import com.insightsurfface.videocrawler.business.video.VideoActivity;
import com.insightsurfface.videocrawler.db.DbAdapter;
import com.insightsurfface.videocrawler.listener.OnEmptyBtnClick;
import com.insightsurfface.videocrawler.listener.OnListDialogListener;
import com.insightsurfface.videocrawler.utils.FastClickUtil;
import com.insightsurfface.videocrawler.utils.FileUtils;
import com.insightsurfface.videocrawler.utils.StringUtil;
import com.insightsurfface.videocrawler.utils.VideoUtil;
import com.insightsurfface.videocrawler.widget.dialog.ListDialog;

import java.io.File;
import java.io.IOException;
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
    private final String[] DELETE_LIST = {"从列表中删除", "彻底删除"};

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
                toFileChooserActivity();
            }

            @Override
            public void onTitleClick() {
                Intent intent = new Intent(getActivity(), TestActivity.class);
                startActivity(intent);
            }
        });
        LayoutAnimationController controller = new LayoutAnimationController(AnimationUtils.loadAnimation(getActivity(), R.anim.recycler_load));
        refreshRcv.setLayoutAnimation(controller);
    }

    private void toFileChooserActivity() {
        if (FastClickUtil.isNotFastClick()) {
//                showFileChooser();
            Intent intent = new Intent(getActivity(), FileChooseActivity.class);
            //必须使用 getActivity().startActivityForResult 否则requestCode无法对应
            getActivity().startActivityForResult(intent, 2);
        }
    }

    /**
     * 调用文件选择软件来选择文件
     **/
    public void showFileChooser() {
//        baseToast.showToast("选择视频文件（目前支持3gp,mp4,avi）");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("text/plain");//设置类型和后缀 txt
        intent.setType("video/*");//设置类型和后缀  全部文件
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        getActivity().startActivityForResult(intent, 1);
    }

    public void addVideo(final ArrayList<VideoBean> list) {
        if (null == list || list.size() == 0) {
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(getActivity());
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (VideoBean item : list) {
                    db.insertVideoTableTb(getActivity(), item.getPath(), item.getTitle(), item.getDuration(), 0);
                }
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                videoList = db.queryAllVideos(getActivity());
                mHandler.sendEmptyMessage(UPDATE_LIST);
            }
        }).start();
    }

    public void addVideo(final String path) {
        SingleLoadBarUtil.getInstance().showLoadBar(getActivity());
        new Thread(new Runnable() {
            @Override
            public void run() {
                int duration = VideoUtil.getVideoDuration(getActivity(), Uri.parse(path));

                db.insertVideoTableTb(getActivity(), path, StringUtil.cutString(path, '/', '.'), duration, 0);
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                videoList = db.queryAllVideos(getActivity());
                mHandler.sendEmptyMessage(UPDATE_LIST);
            }
        }).start();
    }

    private void deleteVideo(int pos) {
        db.deleteVideoById(getActivity(), videoList.get(pos).getId());
        mAdapter.remove(pos);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            try {
//                doGetData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doGetData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                videoList = db.queryAllVideos(getActivity());
                mHandler.sendEmptyMessage(UPDATE_LIST);
            }
        }).start();
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

    private void showDeleteDialog(final int pos) {
        NormalDialog dialog = new NormalDialog(getActivity());
        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onOkClick() {
                try {
                    FileUtils.deleteFile(new File(videoList.get(pos).getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                deleteVideo(pos);
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setTitle("是否删除该视频(本地视频也将被删除)?");
        dialog.setOkBtnText("删除");
        dialog.setCancelBtnText("取消");
    }

    private void showDeleteSelectorDialog(final int deletePos) {
        ListDialog listDialog = new ListDialog(getActivity());
        listDialog.setOnListDialogListener(new OnListDialogListener() {
            @Override
            public void onItemClick(String selectedRes, String selectedCodeRes) {
            }

            @Override
            public void onItemClick(String selectedRes) {

            }

            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:
                        deleteVideo(deletePos);
                        break;
                    case 1:
                        showDeleteDialog(deletePos);
                        break;
                }
            }
        });
        listDialog.show();
        listDialog.setOptionsList(DELETE_LIST);
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
                        showDeleteSelectorDialog(position);
                    }
                });
                mAdapter.setOnEmptyBtnClick(new OnEmptyBtnClick() {
                    @Override
                    public void onClick() {
//                        showFileChooser();
                        toFileChooserActivity();
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
