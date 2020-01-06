package com.insightsurfface.videocrawler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.insightsurface.lib.base.BaseRecyclerAdapter;
import com.insightsurface.lib.listener.OnRecycleItemClickListener;
import com.insightsurface.lib.listener.OnRecycleItemLongClickListener;
import com.insightsurfface.videocrawler.R;
import com.insightsurfface.videocrawler.bean.VideoBean;
import com.insightsurfface.videocrawler.listener.OnEmptyBtnClick;
import com.insightsurfface.videocrawler.utils.StringUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Administrator on 2017/11/15.
 * 还款页的还款计划
 */
public class VideoChooserAdapter extends BaseRecyclerAdapter {
    private ArrayList<VideoBean> list = null;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
    private OnEmptyBtnClick mOnEmptyBtnClick;
    private ArrayList<VideoBean> selectedList = new ArrayList<>();

    public VideoChooserAdapter(Context context) {
        super(context);
    }

    @Override
    protected String getEmptyText() {
        return "没有本地视频";
    }

    @Override
    protected String getEmptyBtnText() {
        return "";
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
        return "";
    }

    @Override
    protected <T> ArrayList<T> getDatas() {
        return (ArrayList<T>) list;
    }

    @Override
    protected RecyclerView.ViewHolder getNormalViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chooser, viewGroup, false);
        NormalViewHolder vh = new NormalViewHolder(view);
        return vh;
    }

    @Override
    protected void refreshNormalViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final VideoBean item = list.get(position);
        ((NormalViewHolder) viewHolder).titleTv.setText(item.getTitle());
        ((NormalViewHolder) viewHolder).durationTv.setText(StringUtil.second2Hour(item.getDuration()));
        ((NormalViewHolder) viewHolder).videoCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!selectedList.contains(item)) {
                        selectedList.add(item);
                    }
                } else {
                    selectedList.remove(item);
                }
            }
        });
        ((NormalViewHolder) viewHolder).videoCb.setChecked(null!=selectedList&&selectedList.contains(item));
        ((NormalViewHolder) viewHolder).videoLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NormalViewHolder) viewHolder).videoCb.setChecked(!((NormalViewHolder) viewHolder).videoCb.isChecked());
            }
        });
        ((NormalViewHolder) viewHolder).videoThumbnailIv.setImageBitmap(item.getThumbnail());
    }

    public void setList(ArrayList<VideoBean> list) {
        this.list = list;
    }

    public ArrayList<VideoBean> getList() {
        return list;
    }

    public void setOnEmptyBtnClick(OnEmptyBtnClick onEmptyBtnClick) {
        mOnEmptyBtnClick = onEmptyBtnClick;
    }

    public ArrayList<VideoBean> getSelectedList() {
        return selectedList;
    }

    public void selectAll() {
        selectedList = list;
        notifyDataSetChanged();
    }

    public void removeAllSelected() {
        //因为selectedList已经被赋值为list 所以他们指向同一个资源 如果清空就全清空了
//        selectedList.clear();
        selectedList=new ArrayList<>();
        notifyDataSetChanged();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        public View videoLl;
        public ImageView videoThumbnailIv;
        public TextView durationTv;
        public TextView titleTv;
        public CheckBox videoCb;

        public NormalViewHolder(View view) {
            super(view);
            videoLl = (View) view.findViewById(R.id.video_ll);
            videoThumbnailIv = (ImageView) view.findViewById(R.id.video_thumbnail_iv);
            durationTv = (TextView) view.findViewById(R.id.duration_tv);
            titleTv = (TextView) view.findViewById(R.id.title_tv);
            videoCb = view.findViewById(R.id.video_cb);
        }
    }
}
