package com.ityun.zhihuiyun.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.CallIngBack;
import com.ityun.zhihuiyun.bean.CallStatus;
import com.ityun.zhihuiyun.bean.JoinMember;
import com.ityun.zhihuiyun.bean.Member;
import com.ityun.zhihuiyun.bean.ReceiveMember;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.bean.User;
import com.ityun.zhihuiyun.bean.UserStatus;
import com.ityun.zhihuiyun.event.ChatUpdataEvent;
import com.ityun.zhihuiyun.event.SendTalkEvent;
import com.wm.Constants;
import com.wm.WMIMSdk;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import okhttp3.Call;

/**
 * Created by Administrator on 2018/9/2 0002.
 */
public class GroupChatUtil {

    private static GroupChatUtil getInstance;

    private SysConfBean bean;

    private User user;

    private RtcEngine rtcEngine;

    private List<Member> memberList = new ArrayList<>();

    private boolean isCalling;

    private boolean isMute;

    private boolean isSpeak;

    public static GroupChatUtil getInstance() {
        if (getInstance == null)
            getInstance = new GroupChatUtil();
        return getInstance;
    }

    public GroupChatUtil() {
        bean = App.getInstance().getSysConfBean();
        user = SpUtil.getUser();
        try {
            //            String sign = "13517fdbd47048cf822df1c0bf33bd74";
            rtcEngine = RtcEngine.create(App.context, bean.getThirdparty().getSwsignature(), handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断当前是否有聊天
     *
     * @param groupId         群组的id
     * @param hasChatListener 回调
     */
    public void hasGroupChat(int groupId, HasChatListener hasChatListener) {
        int userid = bean.getAccountid();
        String msgid = "260";
        int groupids = groupId;
        String url = "http://" + bean.getImsvrip() + ":" + bean.getImsvrhttpport() + "/?msgid=" + msgid + "&userid=" + userid + "&groupids=" + groupids + ",";
        OkHttpUtils
                .get()
                .url(url)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                hasChatListener.NoChat();
            }

            @Override
            public void onResponse(String s, int i) {
                Gson gson = new Gson();
                CallIngBack callIngBack = gson.fromJson(s, CallIngBack.class);
                if (callIngBack != null && callIngBack.getCallstatus() != null && callIngBack.getCallstatus().size() != 0) {
                    CallStatus callStatus = callIngBack.getCallstatus().get(0);
                    if (callStatus.getUsercallstatus() != null && callStatus.getUsercallstatus().size() != 0) {
                        int size = 0;
                        for (UserStatus userStatus : callStatus.getUsercallstatus()) {
                            if (userStatus.getCallstatus() == 1) {
                                size++;
                            }
                        }
                        hasChatListener.HasChat(size, callStatus.getCalltype(), callStatus.getCallid());
                    }
                } else {
                    hasChatListener.NoChat();
                }
            }
        });
    }

    /**
     * 邀请聊天
     *
     * @param groupId  群组id
     * @param list     邀请的人员
     * @param type     邀请的类型
     * @param listener 回调监听
     */
    public void invitationChat(int groupId, List<Member> list, int type, JoinChatListener listener) {
        memberList.clear();
        final byte[] bytes = new byte[1024 * 10];
        String sendJson = "{\"userids\":[";
        int m = 0;
        for (Member member : list) {
            if (member.getId() != user.getId()) {
                if (m == (list.size() - 1)) {
                    sendJson = sendJson + "{\"userid\":" + member.getId() + "}";
                } else {
                    sendJson = sendJson + "{\"userid\":" + member.getId() + "},";
                }
            }
            m++;
        }
        if (sendJson.endsWith(",")) {
            sendJson = sendJson.substring(0, sendJson.length() - 1);
        }
        sendJson = sendJson + "]}";
        final String json = sendJson;
        new Thread(() -> {
            memberList.addAll(memberList);
            int i = WMIMSdk.getInstance().ChatStartGroup(groupId, type, json);
            final int callId = Constants.WM_GetCallId(i);
            int errorCode = Constants.WM_GetErrorCode(i);
            if (errorCode == Constants.WM_IM_ErrorCode.WM_IM_ErrorCode_IsCalling) {
                WMIMSdk.getInstance().ChatJoin(callId, bytes, bytes.length);
                String group = new String(bytes);
                String groups = group.trim();
                Gson gson = new Gson();
                JoinMember memberBean = gson.fromJson(groups, JoinMember.class);
                if (memberBean != null && memberBean.getUsers() != null) {
                    List<Member> lists = new ArrayList<>();
                    for (ReceiveMember userMember : memberBean.getUsers()) {
                        Member member = new Member();
                        member.setId(userMember.getUserid());
                        lists.add(member);
                    }
                    filterMember(memberList, list);
                    listener.ChatJoin(callId, memberList);
                    startChatVoice(callId, type);
                }
            } else if (errorCode == Constants.success) {
                memberList.addAll(list);
                listener.ChatSucess(callId, memberList);
                startChatVoice(callId, type);
            } else {
                listener.ChatFail();
            }
        }).start();
    }

    /**
     * 接受邀请
     *
     * @param chatId    聊天id
     * @param sponsorid 邀请人的id
     * @param type      聊天的类型
     * @param listener  回调
     */
    public void agreeChat(int chatId, int sponsorid, int type, JoinChatListener listener) {
        memberList.clear();
        final byte[] bytes = new byte[1024 * 10];
        int chatCalleeAck = WMIMSdk.getInstance().ChatCalleeAck(chatId, sponsorid, 0, bytes, bytes.length);
        if (chatCalleeAck != 0) {
            listener.ChatFail();
        }
        try {
            String group = new String(bytes);
            String json = group.trim();
            //Json的解析类对象
            JsonParser parser = new JsonParser();
            //将Json的String 转成一个JsonArray对象
            JsonArray jsonArray = parser.parse(json).getAsJsonArray();
            Gson gson = new Gson();
            ArrayList<ReceiveMember> userBeanList = new ArrayList<>();
            //加强for循环遍历JsonArray
            for (JsonElement user : jsonArray) {
                //使用GSON，直接转成Bean对象
                ReceiveMember receiveMember = gson.fromJson(user, ReceiveMember.class);
                userBeanList.add(receiveMember);
            }
            for (ReceiveMember receiveMember : userBeanList) {
                Member member = new Member();
                member.setId(receiveMember.getUserid());
                member.setMemberState(receiveMember.getCallstatus());
                memberList.add(member);
            }
            listener.ChatSucess(chatId, memberList);
            startChatVoice(chatId, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束聊天
     */
    public void stopChat(int chatId) {
        WMIMSdk.getInstance().ChatEnd(chatId);
        endChatVoice();
    }

    /**
     * 拒绝聊天邀请
     *
     * @param chatId
     * @param sponsorid
     */
    public void refuseChat(int chatId, int sponsorid) {
        final byte[] bytes = new byte[1024 * 10];
        WMIMSdk.getInstance().ChatCalleeAck(chatId, sponsorid, 1, bytes, bytes.length);
    }

    /**
     * 邀请别人加入
     *
     * @param chatid
     * @param memberList
     */
    public void sendInvitationChat(int chatid, List<Member> memberList) {
        if (memberList == null)
            return;
        String sendJson = "{\"userids\":[";
        int m = 0;
        for (Member member : memberList) {
            if (member.getId() != user.getId()) {
                if (m == (memberList.size() - 1)) {
                    sendJson = sendJson + "{\"userid\":" + member.getId() + "}";
                } else {
                    sendJson = sendJson + "{\"userid\":" + member.getId() + "},";
                }
            }
            m++;
        }
        sendJson = sendJson + "]}";
        WMIMSdk.getInstance().ChatInvite(chatid, sendJson);
    }

    /**
     * 加入群聊
     *
     * @param type
     * @param chatId
     */
    public void joinGroupTalk(int type, int chatId, JoinChatListener listener) {
        memberList.clear();
        new Thread(() -> {
            byte[] bytes = new byte[1024 * 10];
            WMIMSdk.getInstance().ChatJoin(chatId, bytes, bytes.length);
            String group = new String(bytes);
            String groups = group.trim();
            Gson gson = new Gson();
            JoinMember memberBean = gson.fromJson(groups, JoinMember.class);
            if (memberBean != null && memberBean.getUsers() != null) {
                for (ReceiveMember userMember : memberBean.getUsers()) {
                    Member member = new Member();
                    member.setId(userMember.getUserid());
                    member.setMemberState(userMember.getCallstatus());
                    memberList.add(member);
                }
                listener.ChatJoin(chatId, memberList);
                startChatVoice(chatId, type);
            }
        }).start();
    }

    /**
     * 抢麦
     */
    public void robMicOpen() {
        rtcEngine.muteLocalAudioStream(false);
        rtcEngine.setEnableSpeakerphone(true);
    }

    /**
     * 放麦
     */
    public void robMicClose() {
        rtcEngine.muteLocalAudioStream(true);
//        rtcEngine.setEnableSpeakerphone(true);
    }

    /**
     * 静音
     *
     * @param isMute
     */
    public void muteVoice(boolean isMute) {
        this.isMute = isMute;
        rtcEngine.muteLocalAudioStream(isMute);
    }

    /**
     * 外放
     *
     * @param isSpeak
     */
    public void speakerphone(boolean isSpeak) {
        this.isSpeak = isSpeak;
        rtcEngine.setEnableSpeakerphone(isSpeak);
    }

    /**
     * 获取当前是否静音
     *
     * @return
     */
    public boolean isMute() {
        return isMute;
    }

    /**
     * 获取当前是否外放
     *
     * @return
     */
    public boolean isSpeak() {
        return isSpeak;
    }

    /**
     * 增加人员
     *
     * @param addMember
     */
    public void addMember(Member addMember) {
        boolean hasMember = false;
        for (Member member : memberList) {
            if (addMember.getId() == member.getId()) {
                member.setMemberState(1);
                hasMember = true;
            }
        }
        if (!hasMember) {
            addMember.setMemberState(1);
            memberList.add(addMember);
        }
        EventBus.getDefault().post(new ChatUpdataEvent());
    }

    /**
     * 减少成员
     *
     * @param lostmember
     */
    public void lostMember(Member lostmember) {
        for (Member member : memberList) {
            if (lostmember.getId() == member.getId()) {
                member.setMemberState(0);
            }
        }
        EventBus.getDefault().post(new ChatUpdataEvent());
    }

    /**
     * 看看是否要加入成员
     *
     * @param memberList
     * @param newMembers
     */
    private void filterMember(List<Member> memberList, List<Member> newMembers) {
        List<Member> addMembers = new ArrayList<>();
        for (Member member : newMembers) {
            boolean has = false;
            for (Member oldMember : memberList) {
                if (member.getId() == oldMember.getId()) {
                    has = true;
                }
            }
            if (!has) {
                addMembers.add(member);
            }
            memberList.addAll(addMembers);
        }
    }

    /**
     * 获取当前聊天的用户
     *
     * @return
     */
    public List<Member> getMemberList() {
        return memberList;
    }

    public boolean isCalling() {
        return isCalling;
    }

    /**
     * 开始语音
     *
     * @param chatId 聊天id
     */
    private void startChatVoice(int chatId, int type) {
        rtcEngine.enableAudio();
        rtcEngine.joinChannel(null, chatId + "", null, SpUtil.getUser().getId());
        startTime();
        if (type == 8 || type == 14) {
            rtcEngine.muteLocalAudioStream(true);
            rtcEngine.setEnableSpeakerphone(true);
            isSpeak = true;
        } else {
            rtcEngine.setEnableSpeakerphone(false);
        }
        isCalling = true;

    }

    /**
     * 结束语音
     */
    private void endChatVoice() {
        if (rtcEngine != null) {
            rtcEngine.disableAudio();
            rtcEngine.leaveChannel();
        }
        isCalling = false;
        isSpeak = false;
        isMute = false;
        stopTime();
    }

    private int talkTime = 0;
    Timer timer;

    private void startTime() {
        if (timer != null) {
            stopTime();
        }
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                talkTime++;
                if (onTimeCallBack != null)
                    onTimeCallBack.OnCall(talkTime);
            }
        };
        timer.schedule(task, 0, 1000);
    }

    private void stopTime() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            talkTime = 0;
        }
    }

    /**
     * 发送单人聊天的请求
     *
     * @param account
     */
    public void sendSingleTalk(Account account) {
        final String sendJson = "{\"userids\":[" + "{\"userid\":" + account.getId() + "}" + "]}";
        //0是失败其他作为通话ID
        final int i = WMIMSdk.getInstance().ChatStart(Constants.WM_IM_CallType.WM_IM_CallType_Single_Voice, sendJson);
        if (i == 0) {
            //发起失败
            EventBus.getDefault().post(new SendTalkEvent(1, 0, 0));
        } else {
            EventBus.getDefault().post(new SendTalkEvent(0, 0, i));
        }
    }

    /**
     * 离开单人聊天
     */
    public void closeSingleTalk() {
        if (rtcEngine != null) {
            rtcEngine.disableAudio();
            rtcEngine.leaveChannel();
        }
        stopTime();
    }

    private int roomId;

    /**
     * 打开单人聊天
     */
    public void openSingleTalk(int roomId) {
        this.roomId = roomId;
        rtcEngine.enableAudio();
        rtcEngine.setEnableSpeakerphone(false);
        rtcEngine.joinChannel(null, roomId + "", null, SpUtil.getUser().getId());
        startTime();
    }


    OnTimeCallBack onTimeCallBack;

    /**
     * 获取聊天时间的回调方法
     *
     * @param onTimeCallBack
     */
    public void setOnTimeCallBack(OnTimeCallBack onTimeCallBack) {
        this.onTimeCallBack = onTimeCallBack;
    }

    public interface OnTimeCallBack {
        void OnCall(int time);
    }

    public interface HasChatListener {
        void HasChat(int size, int type, int chatId);

        void NoChat();
    }

    public interface JoinChatListener {
        void ChatSucess(int chatId, List<Member> memberList);

        void ChatFail();

        void ChatJoin(int chatId, List<Member> memberList);
    }

    private IRtcEngineEventHandler handler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
        }

        @Override
        public void onError(int err) {
            super.onError(err);
        }

        @Override
        public void onWarning(int warn) {
            super.onWarning(warn);
        }

        @Override
        public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
            super.onAudioVolumeIndication(speakers, totalVolume);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
        }
    };
}
