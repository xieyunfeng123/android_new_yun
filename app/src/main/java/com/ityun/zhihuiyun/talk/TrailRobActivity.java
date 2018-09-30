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
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.bean.IntercomBean;
import com.ityun.zhihuiyun.bean.Member;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.bean.TalkRecieve;
import com.ityun.zhihuiyun.bean.User;
import com.ityun.zhihuiyun.event.ChatUpdataEvent;
import com.ityun.zhihuiyun.event.FreeRobEvent;
import com.ityun.zhihuiyun.event.GroupEvent;
import com.ityun.zhihuiyun.event.RequestRobEvent;
import com.ityun.zhihuiyun.event.SendTalkBackEvent;
import com.ityun.zhihuiyun.event.SendTalkEvent;
import com.ityun.zhihuiyun.home.HomeActivity;
import com.ityun.zhihuiyun.talk.adapter.MemberAdapter;
import com.ityun.zhihuiyun.util.GroupChatUtil;
import com.ityun.zhihuiyun.util.SpUtil;
import com.ityun.zhihuiyun.util.screen.Eyes;
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

public class TrailRobActivity extends BaseActivity implements View.OnTouchListener {

    private GroupInfo groupInfo;

    @BindView(R.id.rob_back)
    ImageView rob_back;

    @BindView(R.id.talk_time)
    TextView talk_time;

    @BindView(R.id.group_request_member)
    ImageView group_request_member;


    @BindView(R.id.talk_refuse)
    LinearLayout talk_refuse;


    @BindView(R.id.group_member)
    GridView group_member;

    @BindView(R.id.talk_accept)
    LinearLayout talk_accept;

    @BindView(R.id.rob_state_ll)
    LinearLayout rob_state_ll;

    @BindView(R.id.request_call_ll)
    LinearLayout request_call_ll;

    private static TextView rob_state_name;

    private static ImageView rob_state_img;

    //好友
    private List<Account> accounts = new ArrayList<>();

    //群成员
    private static List<Member> members = new ArrayList<>();

    private static MemberAdapter adapter;

    //聊天室id
    private static int chatRoomId;


    //接受邀请的信息
    private TalkRecieve receive;

    //群信息
    private GroupInfo recieveGroup;

    private byte[] bytes = new byte[1024];

    public static RobTalkActivity instance;

    private Vibrator vibrator;

    private SysConfBean bean;

    private User user;

    public static RobTalkActivity getInstance() {
        if (instance == null)
            instance = new RobTalkActivity();
        return instance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarColor(this, getResources().getColor(R.color.talk_bg));
        setContentView(R.layout.activity_rob_talk);
        EventBus.getDefault().register(this);
        vibrator = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
        rob_state_name = findViewById(R.id.rob_state_name);
        rob_state_img = findViewById(R.id.rob_state_img);
        bean = App.getInstance().getSysConfBean();
        members.clear();
        ButterKnife.bind(this);
        groupInfo = (GroupInfo) getIntent().getSerializableExtra("group");
        receive = (TalkRecieve) getIntent().getSerializableExtra("receive");
        if (App.accountList != null) {
            accounts.addAll(App.accountList);
        }
        if (groupInfo != null) {
            //发出邀请
            members = (List<Member>) getIntent().getSerializableExtra("members");
            recieveGroup = groupInfo;
            user = SpUtil.getUser();
            Member member = new Member();
            member.setId(user.getId());
            member.setName(user.getUserName());
            member.setMemberState(1);
            members.add(member);
            GroupChatUtil.getInstance().invitationChat(groupInfo.getId(), members, 14, new GroupChatUtil.JoinChatListener() {
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
            rob_state_ll.setVisibility(View.VISIBLE);
            request_call_ll.setVisibility(View.GONE);
        } else if (receive != null) {
            // 被邀请
            chatRoomId = receive.getChatid();
            vibrator.vibrate(new long[]{1000, 1500, 500, 1500, 500,
                    1500, 500,
            }, -1);
            if (receive.isJoin()) {
                rob_state_ll.setVisibility(View.VISIBLE);
                request_call_ll.setVisibility(View.INVISIBLE);
                //聊天界面判断是否有聊天 如果有 且是这个模式 就到这里直接加入聊天室
                GroupChatUtil.getInstance().joinGroupTalk(14, chatRoomId, new GroupChatUtil.JoinChatListener() {
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
                vibrator.cancel();
                rob_state_ll.setVisibility(View.VISIBLE);
                request_call_ll.setVisibility(View.INVISIBLE);
                if (receive != null) {
                    GroupChatUtil.getInstance().agreeChat(chatRoomId, receive.getSponsorid(), 14, new GroupChatUtil.JoinChatListener() {
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
            }
            for (GroupInfo groupInfo : HomeActivity.groupInfos) {
                if (groupInfo.getId() == receive.getGroupId()) {
                    recieveGroup = groupInfo;
                }
            }
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
        rob_state_img.setOnTouchListener(this);
    }

    @OnClick({R.id.talk_refuse, R.id.rob_back, R.id.group_request_member, R.id.group_accept})
    public void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.talk_refuse:
                vibrator.cancel();
                closeVioce();
                break;
            case R.id.rob_back:
                //关闭
                vibrator.cancel();
                closeVioce();
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
            case R.id.group_accept:
                vibrator.cancel();
                if (receive != null) {
                    //直接开吹不比比
                    int chatCalleeAck = WMIMSdk.getInstance().ChatCalleeAck(receive.getChatid(), receive.getSponsorid(), 0, bytes, bytes.length);
                    if (chatCalleeAck != 0) {
                        finish();
                        return;
                    } else {
                        try {
                            String group = new String(bytes, "utf-8");
                            int size = group.lastIndexOf("}");
                            String json = group.substring(0, size + 1);
                            Gson gson = new Gson();
                            MemberBean memberBean = gson.fromJson(json, MemberBean.class);
                            if (memberBean != null && memberBean.getUsers() != null) {
                                for (Member memberUser : members) {
                                    for (UserMember userMember : memberBean.getUsers()) {
                                        if (userMember.getUserid() == memberUser.getId()) {
                                            memberUser.setMemberState(1);
                                        }
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        talk_accept.setVisibility(View.GONE);
                        TalkUtil.getInstance().openRobGroupTalk(receive.getChatid());
                    }
                    rob_state_ll.setVisibility(View.VISIBLE);
                    request_call_ll.setVisibility(View.GONE);
                }
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
     * 添加进来了群成员 刷新界面
     *
     * @param memberList
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void addReceiveEvent(List<Member> memberList) {
        if (memberList != null) {
//            for (Member member : members) {
//                for (Member member1 : memberList) {
//                    if (member.getMemberState() != 1 && member.getId() == member1.getId()) {
//                        member.setMemberState(0);
//                    }
//                }
//            }
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

    /**
     * 别人抢麦成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestRob(RequestRobEvent event) {
        IntercomBean intercomBean = event.getBean();
        if (intercomBean.getChatid() == chatRoomId) {
            Message message = new Message();
            message.what = 1;
            message.obj = intercomBean;
            handler.sendMessage(message);
        }
    }

    /**
     * 别人放麦
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void freeRob(FreeRobEvent event) {
        IntercomBean intercomBean = event.getBean();
        if (intercomBean.getChatid() == chatRoomId) {
            Message message = new Message();
            message.what = 2;
            message.obj = intercomBean;
            handler.sendMessage(message);
        }
    }

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                    break;
                case 1:
                    if (rob_state_img != null) {
                       /* IntercomBean intercomBean = (IntercomBean) msg.obj;
                        for (Member member : members) {
                            if (intercomBean.getUserid() == member.getId()) {
                                member.setMemberState(3);
                            }
                        }
                        adapter.notifyDataSetChanged();*/
                        rob_state_img.setImageResource(R.mipmap.call_onmic);
                        rob_state_name.setText("等待抢麦");
                        rob_state_img.setEnabled(false);
                    }
                    break;
                case 2:
                    if (rob_state_img != null) {
                        IntercomBean intercomBean = (IntercomBean) msg.obj;
                        for (Member member : members) {
                            if (intercomBean.getUserid() == member.getId()) {
                                member.setMemberState(1);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        rob_state_img.setImageResource(R.mipmap.call_nomic);
                        rob_state_name.setText("无人占麦");
                        rob_state_img.setEnabled(true);
                    }
                    break;
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendMessageEvent(SendTalkEvent event) {
        if (event.getResult() != 0) {
            if (event.getResult() != 100) {
                Tost("邀请失败");

                finish();
            }
        } else {
            //申请聊天成功
            chatRoomId = event.getChatId();
            chatFreeMic();
            if (!TextUtils.isEmpty(event.getMemberString())) {
                int size = event.getMemberString().lastIndexOf("}");
                String json = event.getMemberString().substring(0, size + 1);
                Gson gson = new Gson();
                MemberBean memberBean = gson.fromJson(json, MemberBean.class);
                int chatype = Constants.WM_GetCallType(memberBean.getChattype());
                if (chatype != Constants.WM_IM_CallType.WM_IM_CallType_Group_Voice_RobMic) {
                    Tost("当前有其他会话");
                    closeVioce();
                }
                if (memberBean != null && memberBean.getUsers() != null) {
                    for (UserMember userMember : memberBean.getUsers()) {
                        Member member = new Member();
                        member.setId(userMember.getUserid());
                        for (Account account : accounts) {
                            if (account.getId() == member.getId()) {
                                member.setName(account.getName());
                                member.setMemberState(0);
                            }
                        }
                        boolean has = false;
                        for (Member member1 : members) {
                            if (member1.getId() == member.getId()) {
                                has = true;
                            }
                        }
                        if (!has) {
                            members.add(member);
                        }
                    }
                    for (Member memberUser : members) {
                        for (UserMember userMember : memberBean.getUsers()) {
                            if (userMember.getUserid() == memberUser.getId()) {
                                memberUser.setMemberState(1);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
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

    private boolean isDownOnClick;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (!rob_state_img.isEnabled()) {
                return true;
            }
            if (!isDownOnClick) {
                chatRobMic();
                isDownOnClick = true;
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (!isDownOnClick) {
                chatRobMic();
                isDownOnClick = true;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!rob_state_img.isEnabled()) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (isDownOnClick) {
                chatFreeMic();
                isDownOnClick = false;
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (isDownOnClick) {
                chatFreeMic();
                isDownOnClick = false;
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.rob_state_img) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    chatRobMic();
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    if (isRobSucess) {
                        chatFreeMic();
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (isRobSucess) {
                        chatFreeMic();
                    }
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    /**
     * 判断是否抢麦成功
     */
    private boolean isRobSucess = false;

    public void chatRobMic() {
        int robMic = WMIMSdk.getInstance().ChatRobMic(chatRoomId, bytes, bytes.length);
        if (robMic == 0) {
            adapter.notifyDataSetChanged();
            rob_state_img.setImageResource(R.mipmap.call_unmic);
            rob_state_name.setText("松手放麦");
            isRobSucess = true;
//            TalkUtil.getInstance().robMicOpen();
            GroupChatUtil.getInstance().robMicOpen();
        } else {
            isRobSucess = false;
            rob_state_img.setImageResource(R.mipmap.call_nomic);
            rob_state_name.setText("无人占麦");
            Toast.makeText(this, "抢麦失败！", Toast.LENGTH_SHORT).show();
        }
    }

    public void chatFreeMic() {
//        TalkUtil.getInstance().robMicClose();
        WMIMSdk.getInstance().ChatFreeMic(chatRoomId);
        GroupChatUtil.getInstance().robMicClose();
        rob_state_img.setImageResource(R.mipmap.call_nomic);
        rob_state_name.setText("无人占麦");
    }
}
