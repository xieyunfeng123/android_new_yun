package com.ityun.zhihuiyun.talk;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.bean.JoinMember;
import com.ityun.zhihuiyun.bean.Member;
import com.ityun.zhihuiyun.bean.ReceiveMember;
import com.ityun.zhihuiyun.bean.TalkRecieve;
import com.ityun.zhihuiyun.bean.User;
import com.ityun.zhihuiyun.event.ChatJoinGroupEvent;
import com.ityun.zhihuiyun.event.ChatStartGroupFailEvent;
import com.ityun.zhihuiyun.event.ChatStartGroupSuccessEvent;
import com.ityun.zhihuiyun.event.ChatUpdataEvent;
import com.ityun.zhihuiyun.event.GroupEvent;
import com.ityun.zhihuiyun.event.SendTalkBackEvent;
import com.ityun.zhihuiyun.event.SendTalkEvent;
import com.ityun.zhihuiyun.home.HomeActivity;
import com.ityun.zhihuiyun.talk.adapter.MemberAdapter;
import com.ityun.zhihuiyun.util.GroupChatUtil;
import com.ityun.zhihuiyun.util.PermissionUtil;
import com.ityun.zhihuiyun.util.SpUtil;
import com.ityun.zhihuiyun.util.screen.Eyes;
import com.ityun.zhihuiyun.window.GroupTalkWindowService;
import com.wm.Constants;
import com.wm.WMIMSdk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/4 0004.
 * 强拉
 */

public class StrongPullTalkActivity extends BaseActivity {

    private GroupInfo groupInfo;

    @BindView(R.id.group_size)
    ImageView group_size;

    @BindView(R.id.talk_time)
    TextView talk_time;

    @BindView(R.id.group_request_member)
    ImageView group_request_member;

    @BindView(R.id.talk_mute)
    LinearLayout talk_mute;

    @BindView(R.id.talk_refuse)
    LinearLayout talk_refuse;

    @BindView(R.id.talk_hf)
    LinearLayout talk_hf;

    @BindView(R.id.group_member)
    GridView group_member;

    @BindView(R.id.group_mute)
    ImageView group_mute;

    @BindView(R.id.talk_accept)
    LinearLayout talk_accept;

    @BindView(R.id.group_accept)
    ImageView group_accept;

    @BindView(R.id.group_hf)
    ImageView group_hf;

    //好友
    private List<Account> accounts = new ArrayList<>();

    //群成员
    private static List<Member> members = new ArrayList<>();

    private static MemberAdapter adapter;

    //聊天室id
    private static int chatRoomId;

    //正在通话
    public static boolean hasTalk = false;

    //是否结束activity
    public static boolean isFinish = true;

    //接受邀请的信息
    private TalkRecieve receive;

    //群信息
    private GroupInfo recieveGroup;

    private byte[] bytes = new byte[1024];

//    //接受到的群成员
//    private List<Member> serviceMembers;

    //静音
    public static boolean isMute = false;

    //免提
    public static boolean isHF = false;

    public static StrongPullTalkActivity instance;

    private Vibrator vibrator;

    private User user;

    public static StrongPullTalkActivity getInstance() {
        if (instance == null)
            instance = new StrongPullTalkActivity();
        return instance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarColor(this, getResources().getColor(R.color.talk_bg));
        setContentView(R.layout.activity_group_talk);
        EventBus.getDefault().register(this);
        vibrator = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
        members.clear();
        ButterKnife.bind(this);
        groupInfo = (GroupInfo) getIntent().getSerializableExtra("group");
        receive = (TalkRecieve) getIntent().getSerializableExtra("receive");
        if (App.accountList != null) {
            accounts.addAll(App.accountList);
        }
        if (groupInfo != null) {
            //邀请群语音
            members = (List<Member>) getIntent().getSerializableExtra("members");
            recieveGroup = groupInfo;
            for (Member member : members) {
                member.setMemberState(0);
            }
            user = SpUtil.getUser();
            Member member = new Member();
            member.setId(user.getId());
            member.setName(user.getUserName());
            member.setMemberState(1);
            members.add(member);
            GroupChatUtil.getInstance().invitationChat(groupInfo.getId(), members, 13, new GroupChatUtil.JoinChatListener() {
                @Override
                public void ChatSucess(int chatId, List<Member> memberList) {
                    chatRoomId = chatId;
                }

                @Override
                public void ChatFail() {
                    Tost("邀请失败");
                    finish();
                }

                @Override
                public void ChatJoin(int chatId, List<Member> memberList) {
                    chatRoomId = chatId;
                    members.clear();
                    members.addAll(memberList);
                    adapter.notifyDataSetChanged();
                }
            });
        } else if (receive != null) {
            //被邀请群语音
            chatRoomId = receive.getChatid();
            vibrator.vibrate(new long[]{1000, 1500, 500, 1500, 500,
                    1500, 500,
            }, -1);
            talk_accept.setVisibility(View.GONE);
            talk_mute.setVisibility(View.VISIBLE);
            talk_hf.setVisibility(View.VISIBLE);
            if (receive.isJoin()) {
                //聊天界面判断是否有聊天 如果有 且是这个模式 就到这里直接加入聊天室
                GroupChatUtil.getInstance().joinGroupTalk(13, chatRoomId, new GroupChatUtil.JoinChatListener() {
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
                                members.addAll(memberList);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            } else {
                GroupChatUtil.getInstance().agreeChat(chatRoomId, receive.getSponsorid(), 13, new GroupChatUtil.JoinChatListener() {
                    @Override
                    public void ChatSucess(int chatId, List<Member> memberList) {
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
            for (GroupInfo groupInfo : HomeActivity.groupInfos) {
                if (groupInfo.getId() == receive.getGroupId()) {
                    recieveGroup = groupInfo;
                }
            }
        } else {
            //悬浮窗口跳转过来的
            recieveGroup = (GroupInfo) getIntent().getSerializableExtra("servicegroup");
            chatRoomId = getIntent().getIntExtra("chatRoomId", 0);
            members.clear();
            members.addAll(GroupChatUtil.getInstance().getMemberList());
        }
        adapter = new MemberAdapter(this);
        adapter.setData(members);
        group_member.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        GroupChatUtil.getInstance().setOnTimeCallBack(new GroupChatUtil.OnTimeCallBack() {
            @Override
            public void OnCall(int time) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (talk_time != null) {
                            talk_time.setText(intToTime(time));
                        }
                    }
                });
            }
        });
        if (GroupChatUtil.getInstance().isMute()) {
            group_mute.setImageResource(R.mipmap.jingyin);
        } else {
            group_mute.setImageResource(R.mipmap.jingyin_yes);
        }
        if (GroupChatUtil.getInstance().isSpeak()) {
            group_hf.setImageResource(R.mipmap.kuoyin);
        } else {
            group_hf.setImageResource(R.mipmap.kuoyin_yes);
        }
    }

    @OnClick({R.id.group_hf, R.id.talk_refuse, R.id.group_mute, R.id.group_size, R.id.group_request_member})
    public void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.group_mute:
                if (GroupChatUtil.getInstance().isMute()) {
                    group_mute.setImageResource(R.mipmap.jingyin);
                } else {
                    group_mute.setImageResource(R.mipmap.jingyin_yes);
                }
                GroupChatUtil.getInstance().muteVoice(!GroupChatUtil.getInstance().isMute());
                break;
            case R.id.group_hf:
                if (GroupChatUtil.getInstance().isSpeak()) {
                    group_hf.setImageResource(R.mipmap.kuoyin);
                } else {
                    group_hf.setImageResource(R.mipmap.kuoyin_yes);
                }
                GroupChatUtil.getInstance().speakerphone(!GroupChatUtil.getInstance().isSpeak());
                break;
            case R.id.talk_refuse:
                vibrator.cancel();
                closeVioce();
                break;
            case R.id.group_size:
                //打开悬浮窗
                if (PermissionUtil.getAppOps(this)) {
                    Intent intent = new Intent(this, GroupTalkWindowService.class);
                    intent.putExtra("type",13);
                    intent.putExtra("chatRoomId", chatRoomId);
                    intent.putExtra("group", recieveGroup);
                    startService(intent);
                    finish();
                } else {
                    Tost("没有悬浮窗口权限，请打开相关权限！");
                }
                break;
            case R.id.group_request_member:
                List<Member> allMember = recieveGroup.getMember();
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
     * 关闭聊天
     */
    private void closeVioce() {
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

    /**
     * 邀请成功
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ChatStartGroupSuccessEvent(ChatStartGroupSuccessEvent event) {
        chatRoomId = event.getChatId();
    }

    /**
     * 邀请失败
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ChatStartGroupFailEvent(ChatStartGroupFailEvent event) {
        chatRoomId = event.getChatId();
        Tost("邀请失败");
        hasTalk = false;
        isHF = false;
        isMute = false;
        finish();
    }

    /**
     * 有人加入会话
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ChatJoinGroupEvent(ChatJoinGroupEvent event) {
        chatRoomId = event.getChatId();
        if (!TextUtils.isEmpty(event.getMemberString())) {
            Gson gson = new Gson();
            JoinMember memberBean = gson.fromJson(event.getMemberString(), JoinMember.class);
            int chatype = Constants.WM_GetCallType(memberBean.getChattype());
            if (chatype != Constants.WM_IM_CallType.WM_IM_CallType_Group_Voice) {
                Tost("当前有其他会话");
                closeVioce();
            }
            if (memberBean != null && memberBean.getUsers() != null) {
                for (ReceiveMember userMember : memberBean.getUsers()) {
                    Member member = new Member();
                    member.setId(userMember.getUserid());
                    members.add(member);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 添加进来了群成员 刷新界面
     *
     * @param groupEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addEvent(GroupEvent groupEvent) {
        if (adapter == null || !hasTalk)
            return;
        if (groupEvent.getChatid() == chatRoomId) {
            boolean hasMember = false;
            for (Member member : members) {
                if (member.getId() == groupEvent.getUserid()) {
                    member.setMemberState(1);
                    hasMember = true;
                }
            }
            if (!hasMember) {
                for (Account account : accounts) {
                    if (account.getId() == groupEvent.getUserid()) {
                        Member member = new Member();
                        member.setName(account.getName());
                        member.setMemberState(1);
                        members.add(member);
                    }
                }
            }
            handler.sendEmptyMessage(0);
        }
    }

    public static void lostMember(SendTalkEvent event) {
        if (!hasTalk)
            return;
        if (adapter != null && event.getResult() == 50 && event.getChatId() == chatRoomId) {
            //有人离开
            for (Member member : members) {
                if (event.getType() == member.getId())
                    member.setMemberState(2);
            }
            handler.sendEmptyMessage(0);
        }
    }

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }
    };

    /**
     * 添加进来了群成员 刷新界面
     *
     * @param memberList
     */
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

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        isFinish = true;
    }

    /**
     * 发送的邀请得到回应
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recieveTalkBackEvent(SendTalkBackEvent event) {
        if (event.getResponse() == 1) {
            //拒绝邀请
            for (Member member : members) {
                if (member.getId() == event.getUserid()) {
                    member.setMemberState(2);
                }
            }
            adapter.notifyDataSetChanged();
        } else {
            //同意邀请
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

    /**
     * int 转 时间
     *
     * @param time
     * @return
     */
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

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
