package com.ityun.zhihuiyun.window;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.bean.Member;
import com.ityun.zhihuiyun.talk.TalkUtil;
import com.ityun.zhihuiyun.teamviewer.SendVideoActivity;
import com.ityun.zhihuiyun.util.DensityUtil;
import com.ityun.zhihuiyun.util.GroupChatUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TeamViewerTalkWindowService extends Service implements View.OnClickListener {
    /* 创建浮动窗口布局参数对象 */
    private WindowManager.LayoutParams params;
    /* 悬浮窗 */
    private WindowManager windowManager;

    private RelativeLayout rl_window;

    private TextView window_time;

    private RelativeLayout view;

    private int chatRoomId;

    private boolean starting;

    public static List<Member> members = new ArrayList<>();

    private boolean isClick = false;

    private String name;

    private GroupInfo groupInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        start();
    }

    private void start() {
        params = new WindowManager.LayoutParams();
        // 获取屏幕宽高
        windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT < 19) {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else if (Build.VERSION.SDK_INT <= 24) {
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else if (Build.VERSION.SDK_INT == 25) {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        // 设置window不会获取焦点
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.x = 0;
        params.y = 0;
        params.width = DensityUtil.dip2px(App.context, 48);
        params.height = DensityUtil.dip2px(App.context, 72);
        LayoutInflater inflater = LayoutInflater.from(this);
        view = (RelativeLayout) inflater.inflate(
                R.layout.window_talk, null);
        rl_window = view.findViewById(R.id.window_rl);
        window_time = view.findViewById(R.id.window_time);
        windowManager.addView(view, params);
        rl_window.setOnClickListener(this);
        starting = true;
        updateTime();
    }

    /**
     * 更新时间
     */
    private void updateTime() {
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
            }
        }
    };

    private void stop() {
        Intent intent = new Intent(this, SendVideoActivity.class);
        intent.putExtra("chatRoomId", chatRoomId);
        intent.putExtra("members", (Serializable) members);
        intent.putExtra("name", name);
        intent.putExtra("servicegroup",groupInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        windowManager.removeView(view);
        stopSelf();
        starting = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        members.clear();
        List<Member> mlist = (List<Member>) intent.getSerializableExtra("members");
        if (mlist != null) {
            members.addAll(mlist);
        }
        chatRoomId = intent.getIntExtra("chatRoomId", 0);
        name = intent.getStringExtra("name");
        groupInfo= (GroupInfo) intent.getSerializableExtra("group");
        return super.onStartCommand(intent, flags, startId);
    }

    public static void upDataMember(int id, int onLine) {
        if (members != null) {
            for (Member member : members) {
                if (id == member.getId()) {
                    if (onLine == 0) {
                        member.setMemberState(2);
                    } else {
                        member.setMemberState(1);
                    }
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
            windowManager.removeView(view);
            starting = false;
        }
        super.onDestroy();
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

    @Override
    public void onClick(View v) {
        isClick = true;
        stop();
    }
}
