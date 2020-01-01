package com.insightsurfface.videocrawler.business.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.insightsurface.lib.base.BaseFragment;
import com.insightsurfface.videocrawler.R;

import androidx.annotation.Nullable;

public class UserFragment extends BaseFragment implements View.OnClickListener {
    private ImageView userIv;
    private TextView userNameTv;
    private TextView logoutTv;
    private RelativeLayout feedbackRl;
    private RelativeLayout folderRl;
    private RelativeLayout keyRl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_user, null);
        initUI(mainView);
        return mainView;
    }

    private void initUI(View view) {
        userIv = (ImageView) view.findViewById(R.id.user_iv);
        userNameTv = (TextView) view.findViewById(R.id.user_name_tv);
        folderRl = (RelativeLayout) view.findViewById(R.id.folder_rl);
        keyRl = (RelativeLayout) view.findViewById(R.id.key_rl);
        feedbackRl = (RelativeLayout) view.findViewById(R.id.feedback_rl);
        logoutTv = (TextView) view.findViewById(R.id.logout_tv);

        userIv.setOnClickListener(this);
        folderRl.setOnClickListener(this);
        keyRl.setOnClickListener(this);
        feedbackRl.setOnClickListener(this);
        logoutTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.user_iv:
                break;
            case R.id.key_rl:
                break;
            case R.id.feedback_rl:
                break;
            case R.id.logout_tv:
                break;
        }
        if (null != intent) {
            startActivity(intent);
        }
    }
}
