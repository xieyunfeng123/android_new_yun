package com.ityun.zhihuiyun.teamviewer;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.bean.Member;
import com.ityun.zhihuiyun.bean.TalkRecieve;
import com.ityun.zhihuiyun.bean.User;
import com.ityun.zhihuiyun.event.ChatUpdataEvent;
import com.ityun.zhihuiyun.event.GroupEvent;
import com.ityun.zhihuiyun.event.SendTalkBackEvent;
import com.ityun.zhihuiyun.event.SendTalkEvent;
import com.ityun.zhihuiyun.home.HomeActivity;
import com.ityun.zhihuiyun.talk.MemberBean;
import com.ityun.zhihuiyun.talk.SelectMemberActiviity;
import com.ityun.zhihuiyun.talk.TalkUtil;
import com.ityun.zhihuiyun.talk.UserMember;
import com.ityun.zhihuiyun.teamviewer.adapter.SendVideoAdapter;
import com.ityun.zhihuiyun.util.GroupChatUtil;
import com.ityun.zhihuiyun.util.PermissionUtil;
import com.ityun.zhihuiyun.util.SpUtil;
import com.ityun.zhihuiyun.view.CircleTextView;
import com.ityun.zhihuiyun.window.TeamViewerTalkWindowService;
import com.wm.Constants;
import com.wm.WMIMSdk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/7/24 0024.
 * 远程协助-发送语音
 */
public class SendVideoActivity extends BaseActivity {

    /* 接听语音请求 */
    @BindView(R.id.rl_receive)
    RelativeLayout rl_receive;

    /* 拒绝语音请求 */
    @BindView(R.id.rl_refuse)
    RelativeLayout rl_refuse;

    /* 挂断语音聊天 */
    @BindView(R.id.refuse)
    RelativeLayout refuse;

    /* 发送视频头布局 */
    @BindView(R.id.rl_send_head)
    RelativeLayout send_head;

    /* 发送视频界面文本信息 */
    @BindView(R.id.tv_send_text)
    TextView send_text;

    /* 发送视频界面底部布局 */
    @BindView(R.id.ll_send_foot)
    LinearLayout send_foot;

    /* 静音 */
    @BindView(R.id.rl_mute)
    RelativeLayout rl_mute;

    /* 免提 */
    @BindView(R.id.iv_hf)
    ImageView iv_hf;

    @BindView(R.id.receive_head)
    RelativeLayout receive_head;

    @BindView(R.id.receive_foot)
    RelativeLayout receive_foot;

    @BindView(R.id.teamviewer_memeber)
    RecyclerView rv_member;

    /* 摄像头 */
    @BindView(R.id.iv_camera)
    ImageView camera;

    @BindView(R.id.tv_text)
    TextView tv_groupName;

    @BindView(R.id.cv_head)
    CircleTextView cv_head;

    @BindView(R.id.cv_name)
    TextView cv_name;

    /* 时间 */
    @BindView(R.id.time)
    TextView tv_time;

    @BindView(R.id.video_portrait)
    RelativeLayout video_portrait;

    @BindView(R.id.text)
    TextView text;

    /* 收起 */
    @BindView(R.id.iv_narrow)
    ImageView iv_narrow;

    @BindView(R.id.rl_invite)
    RelativeLayout rl_invite;

    @BindView(R.id.iv_mute)
    ImageView iv_mute;

    public static SendVideoActivity instance;

    // 好友
    private List<Account> accounts = new ArrayList<>();

    private static List<Member> members = new ArrayList<>();

    private static SendVideoAdapter adapter;

    //聊天室id
    private static int chatRoomId;

    //接受邀请的信息
    private TalkRecieve receive;

    //群信息
    private GroupInfo group;

    private GroupInfo receiveInfo;

    private byte[] bytes = new byte[1024];

    private Vibrator vibrator;

    //接受到的群成员
    private List<Member> serviceMembers;

    private User user;

    private String name;

    public static SendVideoActivity getInstance() {
        if (instance == null) {
            instance = new SendVideoActivity();
        }
        return instance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teamviewer_send_video);
        ButterKnife.bind(this);
        members.clear();
        // 获取当前群组信息
        adapter = new SendVideoAdapter(members, this);
        group = (GroupInfo) getIntent().getSerializableExtra("group");
        receive = (TalkRecieve) getIntent().getSerializableExtra("receive");
        serviceMembers = (List<Member>) getIntent().getSerializableExtra("members");
        vibrator = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{1000, 1500, 500, 1500, 500,
                1500, 500,
        }, -1);
        EventBus.getDefault().register(this);
        initData();
        setTime();
    }

    private void initData() {
        // 获取所有用户
        if (App.accountList != null) {
            accounts.addAll(App.accountList);
        }
        user = SpUtil.getUser(); // 获取当前用户

        // 接收到邀请通知
        if (receive != null) {
            chatRoomId = receive.getChatid();
            if (receive.isJoin()) {
                //聊天界面判断是否有聊天 如果有 且是这个模式 就到这里直接加入聊天室
                GroupChatUtil.getInstance().joinGroupTalk(12, chatRoomId, new GroupChatUtil.JoinChatListener() {
                    @Override
                    public void ChatSucess(int chatId, List<Member> memberList) {

                    }

                    @Override
                    public void ChatFail() {
                    }

                    @Override
                    public void ChatJoin(int chatId, List<Member> memberList) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatRoomId = chatId;
                                members.clear();
                                members.clear();
                                for (Member member : memberList) {
                                    if (member.getMemberState() != 0) {
                                        member.setMemberState(1);
                                    }
                                }
                                members.addAll(memberList);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            } else {
                // 接收到邀请通知
                    GroupChatUtil.getInstance().agreeChat(chatRoomId, receive.getSponsorid(), 12, new GroupChatUtil.JoinChatListener() {
                        @Override
                        public void ChatSucess(int chatId, List<Member> memberList) {
                            if(adapter!=null)
                            {
                                members.clear();
                                for (Member member : memberList) {
                                    if (member.getMemberState() != 0) {
                                        member.setMemberState(1);
                                    }
                                }
                                members.addAll(memberList);
                                adapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void ChatFail() {

                        }

                        @Override
                        public void ChatJoin(int chatId, List<Member> memberList) {

                        }
                    });
                setLayout();
            }
            for (GroupInfo group :  HomeActivity.groupInfos) {
                if (group.getId() == receive.getGroupId()) {
                    name = group.getName();
                    receiveInfo = group;
                }
            }
        } else {
            // 悬浮窗口
            chatRoomId = getIntent().getIntExtra("chatRoomId", 0);
            receiveInfo = (GroupInfo) getIntent().getSerializableExtra("servicegroup");
            //悬浮窗口跳转过来的
            members.addAll(GroupChatUtil.getInstance().getMemberList());
            name = getIntent().getStringExtra("name");
        }
        setName(name);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_member.setLayoutManager(lm);
        rv_member.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.rl_receive, R.id.rl_refuse, R.id.refuse, R.id.iv_hf, R.id.rl_mute, R.id.iv_narrow, R.id.rl_invite})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_receive:
                if (receive != null) {
                    GroupChatUtil.getInstance().agreeChat(chatRoomId, receive.getSponsorid(), 9, new GroupChatUtil.JoinChatListener() {
                        @Override
                        public void ChatSucess(int chatId, List<Member> memberList) {
                            members.clear();
                            members.addAll(memberList);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void ChatFail() {

                        }

                        @Override
                        public void ChatJoin(int chatId, List<Member> memberList) {

                        }
                    });
                }
                name = receiveInfo.getName();
//                setName(name);
                setLayout();
                break;
            //拒绝聊天
            case R.id.rl_refuse:
                closeVoice();
                break;
            //挂断电话
            case R.id.refuse:
                closeVoice();
                break;
            //免提
            case R.id.iv_hf:
                if (GroupChatUtil.getInstance().isSpeak()) {
                    iv_hf.setImageResource(R.mipmap.kuoyin);
                } else {
                    iv_hf.setImageResource(R.mipmap.kuoyin_yes);
                }
                GroupChatUtil.getInstance().speakerphone(!GroupChatUtil.getInstance().isSpeak());
                break;
            case R.id.rl_mute:
                if (GroupChatUtil.getInstance().isMute()) {
                    iv_mute.setImageResource(R.mipmap.jingyin);
                } else {
                    iv_mute.setImageResource(R.mipmap.jingyin_yes);
                }
                GroupChatUtil.getInstance().muteVoice(!GroupChatUtil.getInstance().isMute());
                break;
            case R.id.iv_narrow:
                // 缩小窗口
                if (PermissionUtil.getAppOps(this)) {
                    Intent intent = new Intent(this, TeamViewerTalkWindowService.class);
                    intent.putExtra("chatRoomId", chatRoomId);
                    intent.putExtra("members", (Serializable) members);
                    intent.putExtra("type", 12);
                    if (receiveInfo != null) {
                        intent.putExtra("name", receiveInfo.getName());
                    } else {
                        intent.putExtra("name", tv_groupName.getText().toString());
                    }
                    intent.putExtra("group", receiveInfo);
                    startService(intent);
                    finish();
                } else {
                    Tost("没有悬浮窗口权限，请打开权限！");
                }
                break;
            case R.id.rl_invite:
                List<Member> allMember = receiveInfo.getMember();
                List<Member> memberList = new ArrayList<>();
                for (Member member : allMember) {
                    boolean has = false;
                    for (Member talkMember : members) {
                        if (talkMember.getId() == member.getId() && (talkMember.getMemberState() == 1)) {
                            has = true;
                        }
                    }
                    if (!has) {
                        memberList.add(member);
                    }
                }
                for (Member member : memberList) {
                    for (Account account : accounts) {
                        if (account.getId() == member.getId()) {
                            member.setName(account.getName());
                            member.setMemberState(0);
                        }
                    }
                }
                Intent intent = new Intent(this, SelectMemberActiviity.class);
                intent.putExtra("members", (Serializable) memberList);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 获取群名称
     */
    private void setName(String receiveName) {
        try {
            // 设置名称
            String name = URLDecoder.decode(receiveName, "utf-8");
            tv_groupName.setText(name);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听时间
     */
    private void setTime() {
        GroupChatUtil.getInstance().setOnTimeCallBack(new GroupChatUtil.OnTimeCallBack() {
            @Override
            public void OnCall(int time) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (tv_time != null) {
                            tv_time.setText(intToTime(time));
                        }
                    }
                });
            }
        });
    }

    /**
     * 设置接收布局
     */
    private void setLayout() {
        receive_head.setVisibility(View.VISIBLE);
        receive_foot.setVisibility(View.VISIBLE);
        send_head.setVisibility(View.GONE);
        send_text.setVisibility(View.GONE);
        send_foot.setVisibility(View.GONE);
    }

    /**
     * 关闭聊天
     */
    private void closeVoice() {
        if (!GroupChatUtil.getInstance().isCalling()) {
            GroupChatUtil.getInstance().refuseChat(chatRoomId, receive.getSponsorid());
        } else {
            GroupChatUtil.getInstance().stopChat(chatRoomId);
        }
        finish();
    }

    /**
     * 人员変更的通知
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updataMember(ChatUpdataEvent event) {
        members.clear();
        members.addAll(GroupChatUtil.getInstance().getMemberList());
        adapter.notifyDataSetChanged();
    }

    private static Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void addReceiveEvent(List<Member> memberList) {
        if (memberList != null) {
            for (Member member : memberList) {
                boolean has = false;
                for (Member talkMember : members) {
                    if (talkMember.getId() == member.getId()) {
                        has = true;
                    }
                }
                if (!has) {
                    members.add(member);
                }
            }
        }
        TalkUtil.getInstance().receiveOther(chatRoomId, memberList);
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendMessageEvent(SendTalkEvent event) {
        if (event.getResult() != 0) {
            if (event.getResult() != 100) {
                Tost("邀请失败！");
                finish();
            }
        } else {
            chatRoomId = event.getChatId();
            if (!TextUtils.isEmpty(event.getMemberString())) {
                int size = event.getMemberString().lastIndexOf("}");
                String json = event.getMemberString().substring(0, size + 1);
                Gson gson = new Gson();
                MemberBean memberBean = gson.fromJson(json, MemberBean.class);
                int chatType = Constants.WM_GetCallType(memberBean.getChattype());
                if (chatType != 12) {
                    Tost("当前有其他会话");
                    closeVoice();
                }
                if (memberBean != null && memberBean.getUsers() != null) {
                    for (Member member : members) {
                        for (UserMember userMember : memberBean.getUsers()) {
                            if (userMember.getUserid() == member.getId()) {
                                member.setMemberState(1);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveTalkBackEvent(SendTalkBackEvent event) {
        // 拒绝邀请
        if (event.getResponse() == 1) {
            for (Member member : members) {
                if (member.getId() == event.getUserid()) {
                    member.setMemberState(2);
                }
            }
        } else {
            if (event.getChatid() == chatRoomId) {
                for (Member member : members) {
                    if (member.getId() == event.getUserid()) {
                        member.setMemberState(1);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
}
