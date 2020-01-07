package com.insightsurfface.videocrawler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.insightsurface.lib.base.BaseRecyclerAdapter;
import com.insightsurface.lib.listener.OnRecycleItemClickListener;
import com.insightsurface.lib.listener.OnRecycleItemLongClickListener;
import com.insightsurfface.videocrawler.R;
import com.insightsurfface.videocrawler.bean.VideoBean;
import com.insightsurfface.videocrawler.listener.OnEmptyBtnClick;
import com.insightsurfface.videocrawler.utils.StringUtil;
import com.insightsurfface.videocrawler.widget.imageview.WrapHeightImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Administrator on 2017/11/15.
 * 还款页的还款计划
 */
public class VideoAdapter extends BaseRecyclerAdapter {
    private ArrayList<VideoBean> list = null;
    private OnRecycleItemClickListener onRecycleItemClickListener;
    private OnRecycleItemLongClickListener mOnRecycleItemLongClickListener;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
    private OnEmptyBtnClick mOnEmptyBtnClick;

    public VideoAdapter(Context context) {
        super(context);
    }

    @Override
    protected String getEmptyText() {
        return "暂无视频";
    }

    @Override
    protected String getEmptyBtnText() {
        return "+  添加视频";
    }

    @Override
    protected View.OnClickListener getEmptyBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mOnEmptyBtnClick) {
                    mOnEmptyBtnClick.onClick();
                }
            }
        };
    }

    @Override
    protected String getListEndText() {
        return "更多请进入文件夹查看";
    }

    @Override
    protected <T> ArrayList<T> getDatas() {
        return (ArrayList<T>) list;
    }

    @Override
    protected RecyclerView.ViewHolder getNormalViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video, viewGroup, false);
        NormalViewHolder vh = new NormalViewHolder(view);
        return vh;
    }

    @Override
    protected void refreshNormalViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final VideoBean item = list.get(position);
        ((NormalViewHolder) viewHolder).titleTv.setText(item.getTitle());
        ((NormalViewHolder) viewHolder).durationTv.setText(StringUtil.second2Hour(item.getDuration()));
        ((NormalViewHolder) viewHolder).videoLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onRecycleItemClickListener) {
                    onRecycleItemClickListener.onItemClick(position);
                }
            }
        });
        ((NormalViewHolder) viewHolder).videoThumbnailIv.setBitmap(item.getThumbnail());
        ((NormalViewHolder) viewHolder).videoLl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != mOnRecycleItemLongClickListener) {
                    mOnRecycleItemLongClickListener.onItemLongClick(position);
                }
                return true;
            }
        });
    }

    public void setList(ArrayList<VideoBean> list) {
        this.list = list;
    }

    public ArrayList<VideoBean> getList() {
        return list;
    }

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        this.onRecycleItemClickListener = onRecycleItemClickListener;
    }

    public void setOnRecycleItemLongClickListener(OnRecycleItemLongClickListener onRecycleItemLongClickListener) {
        mOnRecycleItemLongClickListener = onRecycleItemLongClickListener;
    }

    public void setOnEmptyBtnClick(OnEmptyBtnClick onEmptyBtnClick) {
        mOnEmptyBtnClick = onEmptyBtnClick;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        public View videoLl;
        public WrapHeightImageView videoThumbnailIv;
        public TextView durationTv;
        public TextView titleTv;

        public NormalViewHolder(View view) {
            super(view);
            videoLl = (View) view.findViewById(R.id.video_ll);
            videoThumbnailIv = (WrapHeightImageView) view.findViewById(R.id.video_thumbnail_iv);
            durationTv = (TextView) view.findViewById(R.id.duration_tv);
            titleTv = (TextView) view.findViewById(R.id.title_tv);
        }
    }
}
