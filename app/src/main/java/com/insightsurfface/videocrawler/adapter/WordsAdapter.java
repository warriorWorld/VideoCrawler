package com.insightsurfface.videocrawler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.insightsurface.lib.base.BaseRecyclerAdapter;
import com.insightsurface.lib.listener.OnRecycleItemClickListener;
import com.insightsurface.lib.listener.OnRecycleItemLongClickListener;
import com.insightsurfface.videocrawler.R;
import com.insightsurfface.videocrawler.bean.VideoBean;
import com.insightsurfface.videocrawler.bean.WordsBookBean;
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
public class WordsAdapter extends BaseRecyclerAdapter {
    private ArrayList<WordsBookBean> list = null;
    private OnRecycleItemClickListener onRecycleItemClickListener;
    private OnRecycleItemLongClickListener mOnRecycleItemLongClickListener;
    private OnRecycleItemClickListener onItemTranslateClickListener;
    private int currentWidth = 0;

    public WordsAdapter(Context context) {
        super(context);
    }

    @Override
    protected String getEmptyText() {
        return "没有生词！";
    }

    @Override
    protected String getEmptyBtnText() {
        return "";
    }

    @Override
    protected View.OnClickListener getEmptyBtnClickListener() {
        return null;
    }

    @Override
    protected String getListEndText() {
        return "没了";
    }

    @Override
    protected <T> ArrayList<T> getDatas() {
        return (ArrayList<T>) list;
    }

    @Override
    protected RecyclerView.ViewHolder getNormalViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_word, viewGroup, false);
        NormalViewHolder vh = new NormalViewHolder(view);
        return vh;
    }

    @Override
    protected void refreshNormalViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final WordsBookBean item = list.get(position);
        ((NormalViewHolder) viewHolder).wordTv.setText(item.getWord() + "(" + item.getTime() + ")");
        ((NormalViewHolder) viewHolder).translateTv.setText(item.getTranslate());
        ((NormalViewHolder) viewHolder).translateTv.setBackgroundColor(context.getResources().getColor(R.color.divide_line));
        ((NormalViewHolder) viewHolder).translateTv.setTextColor(context.getResources().getColor(R.color.transparency));
        ((NormalViewHolder) viewHolder).translateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NormalViewHolder) viewHolder).translateTv.setBackgroundColor(context.getResources().getColor(R.color.transparency));
                ((NormalViewHolder) viewHolder).translateTv.setTextColor(context.getResources().getColor(R.color.main_text_color_gray));
                if (null != onItemTranslateClickListener) {
                    onItemTranslateClickListener.onItemClick(position);
                }
            }
        });
        ((NormalViewHolder) viewHolder).translateTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != mOnRecycleItemLongClickListener) {
                    mOnRecycleItemLongClickListener.onItemLongClick(position);
                }
                return true;
            }
        });
        ((NormalViewHolder) viewHolder).wordIv.setBitmap(item.getWordBp(), currentWidth);
        ((NormalViewHolder) viewHolder).killTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onRecycleItemClickListener) {
                    onRecycleItemClickListener.onItemClick(position);
                }
            }
        });
    }

    public void setList(ArrayList<WordsBookBean> list) {
        this.list = list;
    }

    public ArrayList<WordsBookBean> getList() {
        return list;
    }

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        this.onRecycleItemClickListener = onRecycleItemClickListener;
    }

    public void setOnRecycleItemLongClickListener(OnRecycleItemLongClickListener onRecycleItemLongClickListener) {
        mOnRecycleItemLongClickListener = onRecycleItemLongClickListener;
    }

    public void setCurrentWidth(int currentWidth) {
        this.currentWidth = currentWidth;
    }

    public void remove(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        //必须让后边的刷新 因为上边那个notify是不会重新bindview的所以会使后边的view的position错误
        notifyItemRangeChanged(position, list.size() - position);
    }

    public void add(int position, WordsBookBean data) {
        list.add(position, data);
        notifyItemInserted(position);
        //必须让后边的刷新 因为上边那个notify是不会重新bindview的所以会使后边的view的position错误
        notifyItemRangeChanged(position, list.size() - position);
    }

    public void change(int position, WordsBookBean data) {
        list.remove(position);
        list.add(position, data);
        notifyItemChanged(position);
    }

    public void setOnItemTranslateClickListener(OnRecycleItemClickListener onItemTranslateClickListener) {
        this.onItemTranslateClickListener = onItemTranslateClickListener;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout wordRl;
        public WrapHeightImageView wordIv;
        public TextView wordTv;
        public TextView translateTv;
        public View killTv;

        public NormalViewHolder(View view) {
            super(view);
            wordRl = (RelativeLayout) view.findViewById(R.id.word_rl);
            wordIv = (WrapHeightImageView) view.findViewById(R.id.word_iv);
            wordTv = (TextView) view.findViewById(R.id.word_tv);
            translateTv = (TextView) view.findViewById(R.id.translate_tv);
            killTv = view.findViewById(R.id.kill_tv);
        }
    }
}
