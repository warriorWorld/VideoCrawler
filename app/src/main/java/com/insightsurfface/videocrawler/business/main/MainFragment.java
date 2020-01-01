package com.insightsurfface.videocrawler.business.main;

import android.view.View;

import com.insightsurface.lib.base.BaseRefreshListFragment;
import com.insightsurface.lib.listener.OnDialogClickListener;
import com.insightsurface.lib.listener.OnRecycleItemClickListener;
import com.insightsurface.lib.listener.OnRecycleItemLongClickListener;
import com.insightsurface.lib.widget.dialog.NormalDialog;
import com.insightsurfface.videocrawler.R;
import com.insightsurfface.videocrawler.adapter.VideoAdapter;
import com.insightsurfface.videocrawler.business.bean.VideoBean;

import java.util.ArrayList;

public class MainFragment extends BaseRefreshListFragment {
    private ArrayList<VideoBean> noteList;
    private VideoAdapter mAdapter;

    @Override
    protected void initUI(View v) {
        super.initUI(v);
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
                mAdapter.setList(noteList);
                mAdapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                    }
                });
                mAdapter.setOnRecycleItemLongClickListener(new OnRecycleItemLongClickListener() {
                    @Override
                    public void onItemLongClick(int position) {
                        showDeleteDialog(noteList.get(position).getPath());
                    }
                });
                refreshRcv.setAdapter(mAdapter);
            } else {
                mAdapter.setList(noteList);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            noMoreData();
        }
        noMoreData();
    }
}
