package com.insightsurfface.videocrawler.business.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.insightsurface.lib.base.BaseFragment;
import com.insightsurface.lib.bean.LoginBean;
import com.insightsurface.lib.business.user.LoginActivity;
import com.insightsurface.lib.eventbus.EventBusEvent;
import com.insightsurface.lib.listener.OnDialogClickListener;
import com.insightsurface.lib.utils.ActivityPoor;
import com.insightsurface.lib.widget.dialog.NormalDialog;
import com.insightsurfface.videocrawler.R;
import com.insightsurfface.videocrawler.base.BaseActivity;

import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private MainFragment mMainFragment;
    private UserFragment mUserFragment;
    /**
     * 当前选中页
     */
    private BaseFragment curFragment;
    private View mainV, userV;
    private ImageView mainIv, userIv;
    private TextView homepageBottomTv;
    private TextView userBottomTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainFragment = new MainFragment();
        mUserFragment = new UserFragment();
        switchContent(null, mMainFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        curFragment.onHiddenChanged(false);
    }

    @Override
    protected void initUI() {
        super.initUI();
        mainV = findViewById(R.id.homepage_bottom_ll);
        userV = findViewById(R.id.user_bottom_ll);
        mainIv = (ImageView) findViewById(R.id.homepage_bottom_iv);
        userIv = (ImageView) findViewById(R.id.user_bottom_iv);
        mainV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mMainFragment.scrollToTop();
                return true;
            }
        });
        homepageBottomTv = (TextView) findViewById(R.id.homepage_bottom_tv);
        userBottomTv = (TextView) findViewById(R.id.user_bottom_tv);
        mainV.setOnClickListener(this);
        userV.setOnClickListener(this);
        hideBaseTopBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarFullTransparent();
            setFitSystemWindow(false);
        }
    }

    @Override
    public void onEventMainThread(EventBusEvent event) {
        super.onEventMainThread(event);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    public void switchContent(BaseFragment from, BaseFragment to) {
        if (curFragment != to) {
            curFragment = to;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (!to.isAdded()) { // 先判断是否被add过
                if (null != from) {
                    transaction.hide(from);
                }
                transaction.add(R.id.container, to, to.getFragmentTag())
                        .addToBackStack(to.getTag()).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                if (null != from) {
                    transaction.hide(from);
                }
                transaction.show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
            }
        }
        to.onHiddenChanged(false);
        toggleBottomBar(to);
    }

    private void toggleBottomBar(BaseFragment bf) {
        userBottomTv.setTextColor(getResources().getColor(R.color.main_text_color_gray));
        homepageBottomTv.setTextColor(getResources().getColor(R.color.main_text_color_gray));
        if (bf instanceof MainFragment) {
            homepageBottomTv.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else if (bf instanceof UserFragment) {
            userBottomTv.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    private void showQuitDialog() {
        NormalDialog logoutDialog = new NormalDialog(MainActivity.this);
        logoutDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onOkClick() {

            }

            @Override
            public void onCancelClick() {
                ActivityPoor.finishAllActivity();
            }
        });
        logoutDialog.show();

        logoutDialog.setTitle("确定退出" + getResources().getString(R.string.app_name) + "吗？");
        logoutDialog.setCancelBtnText("退出");
        logoutDialog.setOkBtnText("再逛逛");
        logoutDialog.setCancelable(true);
    }

    @Override
    public void onBackPressed() {
        showQuitDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homepage_bottom_ll:
                switchContent(curFragment, mMainFragment);
                break;
            case R.id.user_bottom_ll:
                switchContent(curFragment, mUserFragment);
                break;
        }
    }
}
