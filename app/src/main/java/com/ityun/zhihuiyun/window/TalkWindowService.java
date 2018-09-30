package com.ityun.zhihuiyun.window;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.bean.TalkRecieve;
import com.ityun.zhihuiyun.talk.SingleTalkActivity;
import com.ityun.zhihuiyun.talk.TalkUtil;
import com.ityun.zhihuiyun.util.DensityUtil;
import com.ityun.zhihuiyun.util.GroupChatUtil;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/6/4 0004.
 */

public class TalkWindowService extends Service implements View.OnClickListener {


    private WindowManager.LayoutParams wmParams;
    // 创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;

    private RelativeLayout window_rl;

    private TextView window_time;

    private RelativeLayout view;

    private Account account;

    private TalkRecieve talkRecieve;

    // 0 单人发送 1 单人邀请
    private int TPYE = 0;

    private int chatRoomId;

    @Override
    public void onCreate() {
        super.onCreate();
        start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        account = (Account) intent.getSerializableExtra("account");
        if (account != null) {
            TPYE = 0;
        }
        talkRecieve = (TalkRecieve) intent.getSerializableExtra("receive");
        if (talkRecieve != null) {
            TPYE = 1;
        }
        chatRoomId = intent.getIntExtra("chatRoomId", 0);
        return super.onStartCommand(intent, flags, startId);

    }

    private void start() {
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT < 19) {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else if (Build.VERSION.SDK_INT <= 24) {
            wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else if (Build.VERSION.SDK_INT == 25) {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.RIGHT | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        // 设置悬浮窗口长宽数据
        wmParams.width = DensityUtil.dip2px(App.context, 48);
        wmParams.height = DensityUtil.dip2px(App.context, 72);
        LayoutInflater inflater = LayoutInflater.from(this);
        view = (RelativeLayout) inflater.inflate(
                R.layout.window_talk, null);
        window_rl = view.findViewById(R.id.window_rl);
        window_time = view.findViewById(R.id.window_time);
        mWindowManager.addView(view, wmParams);
        window_rl.setOnClickListener(this);
        upDataTime();
    }

    private void stop() {
        Intent intent = new Intent(this, SingleTalkActivity.class);
        if (TPYE == 0) {
            intent.putExtra("account", account);
        } else if (TPYE == 1) {
            intent.putExtra("receive", talkRecieve);
        }
        intent.putExtra("chatRoomId", chatRoomId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        mWindowManager.removeView(view);
        stopSelf();
    }

    private boolean isClick = false;

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        if (!isClick) {
            mWindowManager.removeView(view);
        }
        super.onDestroy();
    }

    private void upDataTime() {
        GroupChatUtil.getInstance().setOnTimeCallBack(time -> {
            Message message = new Message();
            message.obj = time;
            handler.sendMessage(message);

        });
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (window_time != null) {
                int time = (int) msg.obj;
                window_time.setText(intToTime(Long.parseLong(time + "")));
//                window_time.setText(intToTime((Long) msg.obj));
            }
        }
    };

    @Override
    public void onClick(View v) {
        isClick = true;
        stop();
    }

    private String intToTime(long time) {
        String hh;
        String mm;
        String ss;
        if (time < 60 * 60) {
            if (time / (60) < 10) {
                mm = "0" + time / (60);
            } else {
                mm = "" + time / (60);
            }
            if (time % (60) < 10) {
                ss = "0" + time % (60);
            } else {
                ss = "" + time % (60);
            }
            return mm + ":" + ss;
        } else {
            long lev;
            if (time / (60 * 60) < 10) {
                hh = "0" + time / (60 * 60);
            } else {
                hh = "" + time / (60 * 60);
            }
            lev = time - (60 * 60);
            if (lev / (60) < 10) {
                mm = "0" + lev / (60);
            } else {
                mm = "" + lev / (60);
            }
            if (lev % (60) < 10) {
                ss = "0" + lev % (60);
            } else {
                ss = "" + lev % (60);
            }
            return hh + ":" + mm + ":" + ss;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
