package com.ityun.zhihuiyun.me;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.util.SpUtil;
import com.ityun.zhihuiyun.util.CacheUtil;
import com.ityun.zhihuiyun.util.screen.Eyes;
import com.ityun.zhihuiyun.view.ProgressDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/11 0011.
 * 系统设置页面
 */

public class SettingActivity extends BaseActivity {

    @BindView(R.id.setting_back)
    ImageView setting_back;

    @BindView(R.id.tbtn_voice)
    ToggleButton voice;

    @BindView(R.id.tbtn_shock)
    ToggleButton shock;

    @BindView(R.id.clear_cache)
    RelativeLayout clear_cache;

    @BindView(R.id.cache_size)
    TextView cacheSize;

    // 获取手机缓存
    private String totalCache;

    // 声音是否打开
    private boolean isVoice = false;

    // 振动是否打开
    private boolean isShock = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarColor(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        // 获取缓存
        totalCache = CacheUtil.getAllCache(getApplicationContext());
        cacheSize.setText(totalCache);
        int shockSp = SpUtil.getShock(this);
        int voiceSp = SpUtil.getVoice(this);
        if (shockSp == 1) {
            shock.setChecked(true);
        }
        if (voiceSp == 1) {
            voice.setChecked(true);
        }
        // 声音
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
        voice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (voice.isChecked()) {
                rt.play();
                SpUtil.setVoice(SettingActivity.this, 1);
            } else {
                rt.stop();
                SpUtil.setVoice(SettingActivity.this, 0);
            }
        });
        // 振动
        final Vibrator vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        shock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                vibrator.vibrate(new long[]{1000, 2000}, -1);
                SpUtil.setShock(SettingActivity.this, 1);
            } else {
                vibrator.cancel();
                SpUtil.setShock(SettingActivity.this, 0);
            }
        });
    }

    @OnClick({R.id.clear_cache, R.id.setting_back})
    public void onClick(View view) {
        switch (view.getId()) {
            // 清除缓存
            case R.id.clear_cache:
                cleanCache();
                break;
            case R.id.setting_back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 清除缓存
     */
    private void cleanCache() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setMessage("确定清除缓存吗？");
        builder.setTitle("提示");
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("确定", (dialog, which) -> {
            final Dialog loadingDialog = ProgressDialog.create(SettingActivity.this,
                    "正在清理……");
            loadingDialog.show();
            if (totalCache.equals("0KB")) {
                Toast.makeText(SettingActivity.this, "没有缓存， 无需清理", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            } else {
                CacheUtil.clearAllCache(getApplicationContext());
                cacheSize.setText("0KB");
                setting_back.postDelayed(() -> {
                    if (loadingDialog != null)
                        loadingDialog.dismiss();
                }, 1500);
            }
        });
        builder.create().show();
    }
}
