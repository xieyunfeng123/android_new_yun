package com.ityun.zhihuiyun.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.bean.Clarity;
import com.ityun.zhihuiyun.bean.User;
import com.ityun.zhihuiyun.login.LoginActivity;
import com.ityun.zhihuiyun.me.ImageQualityActivity;
import com.ityun.zhihuiyun.me.SettingActivity;
import com.ityun.zhihuiyun.me.TempActivity;
import com.ityun.zhihuiyun.talk.TalkUtil;
import com.ityun.zhihuiyun.util.SpUtil;
import com.ityun.zhihuiyun.view.CircleTextView;
import com.wm.WMIMSdk;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2018/5/21 0021.
 * “我的”界面
 */
public class MeFragment extends Fragment {

    private View view;

    /* 显示清晰度 */
    @BindView(R.id.tv_clarity)
    TextView tv_clarity;

    /* 修版本号 */
    @BindView(R.id.me_version)
    LinearLayout version;

    /* 系统设置 */
    @BindView(R.id.me_sysSetting)
    LinearLayout sysSetting;

    @BindView(R.id.exitSys)
    Button exitSys;

    @BindView(R.id.user_image)
    CircleTextView user_image;

    @BindView(R.id.user_name)
    TextView user_name;

    @BindView(R.id.tv_version)
    TextView tv_version;

    private User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, null);
        ButterKnife.bind(this, view);
        user = SpUtil.getUser();
        user_image.setTextString(user.getUserName());
        user_name.setText(user.getUserName());
        initData();
        return view;
    }


    /**
     * 初始化数据
     */
    private void initData() {
        // 获取画面清晰度缓存
        Clarity clarity = SpUtil.getClarity(getActivity());
        if (clarity.getClarity() == 0) {
            tv_clarity.setText("480P");
        } else if (clarity.getClarity() == 1) {
            tv_clarity.setText("720P");
        } else if (clarity.getClarity() == 2) {
            tv_clarity.setText("1080P");
        } else {
            tv_clarity.setText("480P");
        }
        // 获取当前软件版本号
        String localVersion = "";
        try {
            PackageInfo packageInfo = getContext().getApplicationContext().getPackageManager()
                    .getPackageInfo(getContext().getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tv_version.setText(localVersion);
    }

    @OnClick({R.id.me_videoback, R.id.me_da, R.id.me_command, R.id.me_sysSetting, R.id.exitSys})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.me_videoback:
                Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.smarteye.mpu");
                if (intent == null) {//未安装app
                    //提示安装
                    Toast.makeText(getActivity(), "未安装", Toast.LENGTH_LONG).show();
                } else {//安装了App
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setAction(Intent.ACTION_MAIN);
                    startActivity(intent);
                }

        break;
            case R.id.me_da:
                Intent intent3 = getActivity().getPackageManager().getLaunchIntentForPackage("com.vomont.mpu");
                if (intent3 == null) {//未安装app
                    //提示安装
                    Toast.makeText(getActivity(), "未安装", Toast.LENGTH_LONG).show();
                } else {//安装了App
                    intent3.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent3.setAction(Intent.ACTION_MAIN);
                    startActivity(intent3);
                }
                break;
            case R.id.me_command:
                try {
                    Intent intentOhter = new Intent();
                    intentOhter.putExtra("UserName", "UT4");
                    intentOhter.setClassName("com.liandisys.xzvideo.app", "com.liandisys.xzvideo.app.activity.FastLoginActivity");
                    startActivity(intentOhter);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "请添加程序", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                //        Intent intent=        getActivity().getPackageManager().getLaunchIntentForPackage("com.smarteye.mpu");
//                if (intent == null) {//未安装app
//                    //提示安装
//                    Toast.makeText(getActivity(), "未安装", Toast.LENGTH_LONG).show();
//                } else {//安装了App
//                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                    intent.setAction(Intent.ACTION_MAIN);
//                    startActivity(intent);
//                }
                // 画面质量
//                String clarity = tv_clarity.getText().toString().trim();
//                Intent in = new Intent(getActivity(), ImageQualityActivity.class);
//                if (clarity.equals("480P")) {
//                    in.putExtra("clarity", 0);
//                } else if (clarity.equals("720P")) {
//                    in.putExtra("clarity", 1);
//                } else if (clarity.equals("1080P")) {
//                    in.putExtra("clarity", 2);
//                } else {
//                    in.putExtra("clarity", 0);
//                }
//                startActivity(in);
                break;
            case R.id.me_sysSetting:
                // 系统设置
                Intent intent1 = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent1);
                break;
            case R.id.exitSys:
                // 退出系统
                User user = SpUtil.getUser();
                SpUtil.setUser(user.getUserName(), "", 0);
                WMIMSdk.getInstance().UnRegister();
                WMIMSdk.getInstance().Uninit();
                TalkUtil.getInstance().closeGroupTalk();
                Intent intent2 = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent2);
                getActivity().finish();
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }
}
