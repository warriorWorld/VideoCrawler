package com.insightsurfface.videocrawler.business.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.insightsurface.lib.base.BaseFragment;
import com.insightsurface.lib.utils.NumberUtil;
import com.insightsurface.lib.utils.SharedPreferencesUtils;
import com.insightsurfface.stylelibrary.dialog.EditDialog;
import com.insightsurfface.stylelibrary.listener.OnEditResultListener;
import com.insightsurfface.videocrawler.R;
import com.insightsurfface.videocrawler.config.ShareKeys;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;

public class UserFragment extends BaseFragment implements View.OnClickListener {
    private ImageView userIv;
    private TextView dayTv;
    private TextView monthTv;
    private TextView dayOfWeekTv;
    private int currentMonth, currentDay;
    private String currentDayOfWeek;
    private RelativeLayout wordsRl;
    private RelativeLayout shareRl;
    private RelativeLayout keyboardRl;
    private RelativeLayout shelterRl;
    private RelativeLayout jumpFrameRl;
    private TextView qqTv;
    private ClipboardManager clip;//复制文本用
    private final String QQ_GROUP = "782685214";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_user, null);
        clip = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("EEEE");
        currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        currentDayOfWeek = format.format(date);
        initUI(mainView);
        refreshUI();
        return mainView;
    }

    private void initUI(View view) {
        userIv = (ImageView) view.findViewById(R.id.user_iv);
        dayTv = (TextView) view.findViewById(R.id.day_tv);
        monthTv = (TextView) view.findViewById(R.id.month_tv);
        dayOfWeekTv = (TextView) view.findViewById(R.id.day_of_week_tv);
        wordsRl = (RelativeLayout) view.findViewById(R.id.words_rl);
        shareRl = (RelativeLayout) view.findViewById(R.id.share_rl);
        keyboardRl = (RelativeLayout) view.findViewById(R.id.keyboard_rl);
        shelterRl = (RelativeLayout) view.findViewById(R.id.shelter_rl);
        jumpFrameRl = view.findViewById(R.id.jump_frame_rl);
        qqTv = (TextView) view.findViewById(R.id.qq_tv);
        qqTv.setText
                ("获取最新版App，请加qq群：" + QQ_GROUP + "（点击群号可复制）。",
                        TextView.BufferType.SPANNABLE);
        qqTv.setMovementMethod(LinkMovementMethod.getInstance());
        qqTv.setHighlightColor(getResources().getColor(R.color.transparency));
        Spannable spannable = (Spannable) qqTv.getText();
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.main_text_color_gray));
//                ds.setUnderlineText(true);
            }
        }, 0, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//0开始
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                baseToast.showToast("已复制群号！");
                clip.setText(QQ_GROUP);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.tag_blue));
                ds.setUnderlineText(true);
            }
        }, 15, 24 + getResources().getString(R.string.app_name).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.main_text_color_gray));
                ds.setUnderlineText(false);
            }
        }, 24, 34, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        wordsRl.setOnClickListener(this);
        shareRl.setOnClickListener(this);
        keyboardRl.setOnClickListener(this);
        shelterRl.setOnClickListener(this);
        userIv.setOnClickListener(this);
        jumpFrameRl.setOnClickListener(this);
    }

    private void refreshUI() {
        dayTv.setText(NumberUtil.toDoubleNum(currentDay));
        monthTv.setText(currentMonth + "月");
        dayOfWeekTv.setText(currentDayOfWeek);
    }

    private void showShelterOptionDialog() {
        EditDialog dialog = new EditDialog(getActivity());
        dialog.setOnEditResultListener(new OnEditResultListener() {
            @Override
            public void onResult(String text) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (getActivity(), ShareKeys.SHELTER_HEIGHT, Integer.valueOf(text));
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setOnlyNumInput(true);
        dialog.setTitle("遮挡高度设置");
        dialog.setHint("默认值为30dp 仅供参考");
        dialog.setMessage("请输入要设置的遮挡高度(单位：dp)，如需隐藏遮挡请输入0。");
    }

    private void showJumpFrameOptionDialog() {
        EditDialog dialog = new EditDialog(getActivity());
        dialog.setOnEditResultListener(new OnEditResultListener() {
            @Override
            public void onResult(String text) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (getActivity(), ShareKeys.JUMP_FRAME_GAP, Integer.valueOf(text));
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setOnlyNumInput(true);
        dialog.setTitle("跳帧间隔设置");
        dialog.setHint("默认值为5000毫秒 仅供参考");
        dialog.setMessage("请输入要设置的跳帧间隔(单位：毫秒 1秒=1000毫秒)，不需要请输入0。");
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.user_iv:
                break;
            case R.id.shelter_rl:
                showShelterOptionDialog();
                break;
            case R.id.jump_frame_rl:
                showJumpFrameOptionDialog();
                break;
        }
        if (null != intent) {
            startActivity(intent);
        }
    }
}
