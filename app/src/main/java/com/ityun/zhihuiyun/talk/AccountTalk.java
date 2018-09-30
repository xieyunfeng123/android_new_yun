package com.ityun.zhihuiyun.talk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.view.CircleTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ityun.zhihuiyun.base.App.context;

/**
 * Created by Administrator on 2018/5/31 0031.
 */

public class AccountTalk {

    private WindowManager.LayoutParams wmParams;

    // 创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;

    private RelativeLayout layout;

    private static AccountTalk accountTalk;

    private Account account;


    @BindView(R.id.talk_img_name)
    public CircleTextView talk_img_name;

    @BindView(R.id.talk_name)
    public TextView talk_name;


    public static AccountTalk getInstance() {
        if (accountTalk == null)
            accountTalk = new AccountTalk();
        return accountTalk;
    }


    public void singleTalk(Account account) {
        this.account = account;
        initView();
    }


    @SuppressLint("InflateParams")
    private void initView() {
        wmParams = new WindowManager.LayoutParams();
//        wmParams.windowAnimations = R.style.anim_popup_dir;
        mWindowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        // 设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        // 设置图片格式，效果为背景透明
        // wmParams.format = PixelFormat.RGBA_8888;
        //wmParams.flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        //wmParams.flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        // 调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;

        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 0;
        wmParams.y = 0;
        // 设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        LayoutInflater inflater = LayoutInflater.from(context);
        layout = (RelativeLayout) inflater.inflate(
                R.layout.activity_accoutn_talk, null);
        mWindowManager.addView(layout, wmParams);
        ButterKnife.bind(this, layout);
        talk_img_name.setText(account.getName());
        talk_name.setText(account.getName());
    }
}
