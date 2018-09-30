package com.ityun.zhihuiyun.window;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.ityun.zhihuiyun.bean.Member;
import com.ityun.zhihuiyun.bean.TalkRecieve;
import com.ityun.zhihuiyun.event.UpdateMemberEvent;
import com.ityun.zhihuiyun.group.GroupActivity;
import com.ityun.zhihuiyun.talk.GroupTalkActivity;
import com.ityun.zhihuiyun.talk.SingleTalkActivity;
import com.ityun.zhihuiyun.talk.StrongPullTalkActivity;
import com.ityun.zhihuiyun.talk.TalkUtil;
import com.ityun.zhihuiyun.teamviewer.SendVideoActivity;
import com.ityun.zhihuiyun.util.DensityUtil;
import com.ityun.zhihuiyun.util.GroupChatUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/6/4 0004.
 */

public class GroupTalkWindowService extends Service implements View.OnClickListener {

    private WindowManager.LayoutParams wmParams;

    // 创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;

    private RelativeLayout window_rl;

    private TextView window_time;

    private RelativeLayout view;

    private boolean starting;

    private int chatRoomId;

    public static List<Member> memberList = new ArrayList<>();

    private GroupInfo groupInfo;

    private boolean isClick = false;

    private int type;

    private String name;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        memberList.clear();
        List<Member> mlist = (List<Member>) intent.getSerializableExtra("members");
        if (mlist != null) {
            memberList.addAll(mlist);
        }
        chatRoomId = intent.getIntExtra("chatRoomId", 0);
        groupInfo = (GroupInfo) intent.getSerializableExtra("group");
        type = intent.getIntExtra("type", 0);
        name = intent.getStringExtra("name");
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
        starting = true;
    }

    private void stop() {
        Intent intent;
        if (type == 9) {
            if (GroupChatUtil.getInstance().isCalling()) {
                intent = new Intent(this, GroupTalkActivity.class);
            } else {
                intent = new Intent(this, StrongPullTalkActivity.class);
            }
        } else if (type == 13) {
            intent = new Intent(this, StrongPullTalkActivity.class);
        } else {
            intent = new Intent(this, SendVideoActivity.class);
        }

        intent.putExtra("chatRoomId", chatRoomId);
        intent.putExtra("servicegroup", groupInfo);
        intent.putExtra("name", name);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        mWindowManager.removeView(view);
        stopSelf();
        starting = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void upDataMemberEvent(UpdateMemberEvent event) {
        int id = event.id;
        int onLine = event.online;
        if (memberList != null) {
            boolean hasMember = false;
            for (Member member : memberList) {
                if (id == member.getId()) {
                    if (onLine == 0) {
                        member.setMemberState(2);
                    } else {
                        member.setMemberState(1);
                    }
                    hasMember = true;
                }
                if (!hasMember && (onLine == 1)) {
                    Member member1 = new Member();
                    member1.setId(event.getId());
                    member1.setMemberState(1);
                    memberList.add(member1);
                }
            }
        }
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        if (!isClick) {
            mWindowManager.removeView(view);
            starting = false;
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void upDataTime() {
        GroupChatUtil.getInstance().setOnTimeCallBack(new GroupChatUtil.OnTimeCallBack() {
            @Override
            public void OnCall(int time) {
                Message message = new Message();
                message.obj = time;
                handler.sendMessage(message);
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (window_time != null) {
                int time = (int) msg.obj;
                window_time.setText(intToTime(Long.parseLong(time + "")));
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
