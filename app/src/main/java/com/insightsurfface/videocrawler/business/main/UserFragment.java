package com.insightsurfface.videocrawler.business.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.insightsurface.lib.listener.OnDialogClickListener;
import com.insightsurface.lib.utils.NumberUtil;
import com.insightsurface.lib.utils.SharedPreferencesUtils;
import com.insightsurface.lib.widget.dialog.DownloadDialog;
import com.insightsurface.lib.widget.dialog.NormalDialog;
import com.insightsurfface.stylelibrary.listener.OnEditResultListener;
import com.insightsurfface.videocrawler.R;
import com.insightsurfface.videocrawler.business.words.WordsActivity;
import com.insightsurfface.videocrawler.config.Configure;
import com.insightsurfface.videocrawler.config.ShareKeys;
import com.insightsurfface.videocrawler.widget.dialog.VideoEditDialog;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class UserFragment extends BaseFragment implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks {
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
    private DownloadDialog downloadDialog;
    private View killableTimeRl, killPeriodRl;

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
        killableTimeRl = view.findViewById(R.id.killable_time_rl);
        killPeriodRl = view.findViewById(R.id.kill_peroid_rl);
        qqTv = (TextView) view.findViewById(R.id.qq_tv);
        qqTv.setText
                ("获取最新版App，请加qq群：" + QQ_GROUP + "（点击群号可复制），或直接点击下载最新App。",
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
                ds.setColor(getResources().getColor(R.color.colorPrimary));
                ds.setUnderlineText(true);
            }
        }, 15, 24, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.main_text_color_gray));
                ds.setUnderlineText(false);
            }
        }, 24, 37, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                showVersionDialog();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.colorPrimary));
                ds.setUnderlineText(true);
            }
        }, 37, 41, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.main_text_color_gray));
                ds.setUnderlineText(false);
            }
        }, 41, 47, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        wordsRl.setOnClickListener(this);
        shareRl.setOnClickListener(this);
        keyboardRl.setOnClickListener(this);
        shelterRl.setOnClickListener(this);
        userIv.setOnClickListener(this);
        jumpFrameRl.setOnClickListener(this);
        killPeriodRl.setOnClickListener(this);
        killableTimeRl.setOnClickListener(this);
    }

    private void showVersionDialog() {
        NormalDialog versionDialog = new NormalDialog(getActivity());
        versionDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onOkClick() {
                doDownload();
            }

            @Override
            public void onCancelClick() {

            }
        });
        versionDialog.show();

        versionDialog.setTitle("下载最新安装包");
        versionDialog.setMessage("由于云服务已暂停，所以无法获取最新版本号，但是您可以直接下载最新版本，如果最新版本高于当前已安装版本可直接覆盖安装。低于或等于当前版本无影响。\nPS:如果下载失败，请打开VPN后再试。");
        versionDialog.setOkBtnText("下载");
        versionDialog.setCancelable(true);
        versionDialog.setCancelBtnText("取消");
    }

    private void showDownLoadDialog() {
        if (null == downloadDialog) {
            downloadDialog = new DownloadDialog(getActivity());
        }
        downloadDialog.show();
        downloadDialog.setCancelable(false);
    }

    @AfterPermissionGranted(111)
    private void doDownload() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            // Already have permission, do the thing
            // ...
            showDownLoadDialog();
            // 下载apk，自动安装
            FinalHttp client = new FinalHttp();
            // url:下载的地址
            // target:保存的地址，包含文件的名称
            // callback 下载时的回调对象
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/VideoCrawler/apk";
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                client.download(Configure.DOWNLOAD_URL, filePath + "/vc.apk",
                        new AjaxCallBack<File>() {

                            // 下载失败时回调这个方法
                            @Override
                            public void onFailure(Throwable t, String strMsg) {
                                super.onFailure(t, strMsg);
                                if (null != downloadDialog && downloadDialog.isShowing()) {
                                    downloadDialog.dismiss();
                                }
                                baseToast.showToast("下载失败");
                            }

                            // 下载时回调这个方法
                            // count ：下载文件需要的总时间，单位是毫秒
                            // current :当前进度,单位是毫秒
                            @Override
                            public void onLoading(long count, long current) {
                                super.onLoading(count, current);
                                String progress = current * 100 / count + "";
                                Integer integer = Integer.parseInt(progress);
                                downloadDialog.setProgress(integer);
                            }

                            // 下载成功时回调这个方法
                            @Override
                            public void onSuccess(File t) {
                                super.onSuccess(t);
                                // 开始使其显示。
                                if (null != downloadDialog && downloadDialog.isShowing()) {
                                    downloadDialog.dismiss();
                                }
                                baseToast.showToast("下载成功,文件保存在" + t.getPath());
                                Intent intent = new Intent();
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setAction("android.intent.action.VIEW");
                                intent.addCategory("android.intent.category.DEFAULT");
                                intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
                                startActivity(intent);
                            }
                        });
            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
                    111, perms);
        }
    }

    private void refreshUI() {
        dayTv.setText(NumberUtil.toDoubleNum(currentDay));
        monthTv.setText(currentMonth + "月");
        dayOfWeekTv.setText(currentDayOfWeek);
    }

    private void showShelterOptionDialog() {
        VideoEditDialog dialog = new VideoEditDialog(getActivity());
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
        int height = SharedPreferencesUtils.getIntSharedPreferencesData(getActivity(), ShareKeys.SHELTER_HEIGHT, -1);
        if (height != -1) {
            dialog.setEditText(height + "");
        }
    }

    private void showJumpFrameOptionDialog() {
        VideoEditDialog dialog = new VideoEditDialog(getActivity());
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
        int duration = SharedPreferencesUtils.getIntSharedPreferencesData(getActivity(), ShareKeys.JUMP_FRAME_GAP, -1);
        if (duration != -1) {
            dialog.setEditText(duration + "");
        }
    }

    private void showKeyboardSettingDialog() {
        NormalDialog dialog = new NormalDialog(getActivity());
        dialog.show();
        dialog.setTitle("键盘设置");
        dialog.setOkBtnText("完美");
        dialog.setMessage("我看了看，现在这键盘已几近完美，所以没什么好设置的。");
    }

    private void showShareDialog() {
        NormalDialog dialog = new NormalDialog(getActivity());
        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onOkClick() {
                clip.setText(Configure.DOWNLOAD_URL);
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setTitle("分享");
        dialog.setMessage("点击按钮复制App下载链接，该链接为GitHub链接，下载时可能需要VPN。");
        dialog.setOkBtnText("复制链接地址");
        dialog.setCancelBtnText("取消");
    }

    private void showKillableTimeDialog() {
        VideoEditDialog dialog = new VideoEditDialog(getActivity());
        dialog.setOnEditResultListener(new OnEditResultListener() {
            @Override
            public void onResult(String text) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (getActivity(), ShareKeys.KILLABLE_TIME_KEY, Integer.valueOf(text));
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setOnlyNumInput(true);
        dialog.setTitle("可斩次数设置");
        dialog.setHint("默认值为3 仅供参考");
        dialog.setMessage("请输入要设置的可斩次数，如需一次既斩请输入1。");
        int height = SharedPreferencesUtils.getIntSharedPreferencesData(getActivity(), ShareKeys.KILLABLE_TIME_KEY, -1);
        if (height != -1) {
            dialog.setEditText(height + "");
        }
    }

    private void showKillPeriodDialog() {
        VideoEditDialog dialog = new VideoEditDialog(getActivity());
        dialog.setOnEditResultListener(new OnEditResultListener() {
            @Override
            public void onResult(String text) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (getActivity(), ShareKeys.KILL_PERIOD_KEY, Integer.valueOf(text));
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setOnlyNumInput(true);
        dialog.setTitle("已斩单词出现间隔时长设置");
        dialog.setHint("默认值为6小时 仅供参考");
        dialog.setMessage("请输入要设置的已斩单词出现间隔时长（单位：小时）");
        int height = SharedPreferencesUtils.getIntSharedPreferencesData(getActivity(), ShareKeys.KILL_PERIOD_KEY, -1);
        if (height != -1) {
            dialog.setEditText(height + "");
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.words_rl:
                intent = new Intent(getActivity(), WordsActivity.class);
                break;
            case R.id.share_rl:
                showShareDialog();
                break;
            case R.id.keyboard_rl:
                showKeyboardSettingDialog();
                break;
            case R.id.user_iv:
                break;
            case R.id.shelter_rl:
                showShelterOptionDialog();
                break;
            case R.id.jump_frame_rl:
                showJumpFrameOptionDialog();
                break;
            case R.id.killable_time_rl:
                showKillableTimeDialog();
                break;
            case R.id.kill_peroid_rl:
                showKillPeriodDialog();
                break;
        }
        if (null != intent) {
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        baseToast.showToast("已获得授权,请继续!");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
//        baseToast.showToast(getResources().getString(R.string.no_permissions), true);
        if (111 == requestCode) {
            NormalDialog peanutDialog = new NormalDialog(getActivity());
            peanutDialog.show();
            peanutDialog.setTitle("没有文件读写权限,无法更新App!可以授权后重试!");
            peanutDialog.setOkBtnText("确定");
        }
    }
}
