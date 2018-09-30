package com.ityun.zhihuiyun.talk;

import android.app.Activity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.JoinMember;
import com.ityun.zhihuiyun.bean.Member;
import com.ityun.zhihuiyun.bean.ReceiveMember;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.bean.User;
import com.ityun.zhihuiyun.event.ChatJoinGroupEvent;
import com.ityun.zhihuiyun.event.ChatStartGroupFailEvent;
import com.ityun.zhihuiyun.event.ChatStartGroupSuccessEvent;
import com.ityun.zhihuiyun.event.SendTalkEvent;
import com.ityun.zhihuiyun.util.SpUtil;
import com.wm.Constants;
import com.wm.WMIMSdk;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

/**
 * Created by Administrator on 2018/6/1 0001.
 */

public class TalkUtil {

    private static TalkUtil instance;

    private RtcEngine rtcEngine;

    private int roomId;
    private SysConfBean bean;
//    private Thread thread;

    private User user;

    Timer timer;

    public static TalkUtil getInstance() {
        if (instance == null)
            instance = new TalkUtil();
        return instance;
    }

    public void init() {
        bean = App.getInstance().getSysConfBean();
        try {
//            String sign=App.getInstance().getSysConfBean().getSignature();
//            String sign = "13517fdbd47048cf822df1c0bf33bd74";
            rtcEngine = RtcEngine.create(App.context,  bean.getThirdparty().getSwsignature(), handler);
        } catch (Exception e) {
            e.printStackTrace();
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
     * 接受别人的邀请
     *
     * @param chatid
     * @param memberList
     */
    public void receiveOther(int chatid, List<Member> memberList) {
        user = SpUtil.getUser();
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
     * 发送群组邀请
     *
     * @param context    上下文
     * @param groupid    邀请的组id
     * @param memberList 邀请的成员
     * @param strongPull 是否是强拉模式
     */
    public void sendGroupTalk(final Activity context, final int groupid, List<Member> memberList, boolean strongPull) {
        user = SpUtil.getUser();
        final byte[] bytes = new byte[1024 * 10];
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
        if (sendJson.endsWith(",")) {
            sendJson = sendJson.substring(0, sendJson.length() - 1);
        }
        sendJson = sendJson + "]}";
        final String json = sendJson;
        new Thread(() -> {
            int model;
            if (strongPull) {
                model = 13;
            } else {
                model = Constants.WM_IM_CallType.WM_IM_CallType_Group_Voice;
            }
            int i = WMIMSdk.getInstance().ChatStartGroup(groupid, model, json);
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
                }
                EventBus.getDefault().post(new ChatJoinGroupEvent(callId, groups));
                context.runOnUiThread(() -> openGroupTalk(callId));
            } else if (errorCode == Constants.success) {
                EventBus.getDefault().post(new ChatStartGroupSuccessEvent(callId));
                context.runOnUiThread(() -> openGroupTalk(callId));
            } else {
                EventBus.getDefault().post(new ChatStartGroupFailEvent(callId));
            }
        }).start();
    }

    /**
     * 加入群聊
     * @param type
     * @param chatId
     * @param context
     */
    public void  joinGroupTalk(int type, int chatId, Activity context) {
        this.roomId = chatId;
        new Thread(() -> {
            byte[] bytes = new byte[1024 * 10];
            WMIMSdk.getInstance().ChatJoin(chatId, bytes, bytes.length);
            String group = new String(bytes);
            String groups = group.trim();
//                String group = new String(bytes, "utf-8");
            Gson gson = new Gson();
            JoinMember memberBean = gson.fromJson(groups, JoinMember.class);
            if (memberBean != null && memberBean.getUsers() != null) {
                List<Member> lists = new ArrayList<>();
                for (ReceiveMember userMember : memberBean.getUsers()) {
                    Member member = new Member();
                    member.setId(userMember.getUserid());
                    lists.add(member);
                }
            }
            EventBus.getDefault().post(new ChatJoinGroupEvent(chatId, groups));
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rtcEngine.enableAudio();
                    if (type == 8 || type == 14) {
                        rtcEngine.muteLocalAudioStream(true);
                    }
                    rtcEngine.joinChannel(null, roomId + "", null, SpUtil.getUser().getId());
                    rtcEngine.setEnableSpeakerphone(false);
                    startTime();
                }
            });
        }).start();
    }

    /**
     * 远程协助-发送语音
     *
     * @param context
     * @param groupid
     * @param memberList
     */
    public void sendTeamviewerTalk(final Activity context, final int groupid, List<Member> memberList) {
        user = SpUtil.getUser();
        final byte[] bytes = new byte[1024];
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
        final String json = sendJson;
        new Thread(() -> {
            int i = WMIMSdk.getInstance().ChatStartGroup(groupid, 12, json);
            final int callId = Constants.WM_GetCallId(i);
            int errorCode = Constants.WM_GetErrorCode(i);
            if (errorCode == Constants.WM_IM_ErrorCode.WM_IM_ErrorCode_IsCalling) {
                WMIMSdk.getInstance().ChatJoin(callId, bytes, bytes.length);
                try {
                    String group = new String(bytes, "utf-8");
                    EventBus.getDefault().post(new SendTalkEvent(0, 0, callId, group));
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            openGroupTalk(callId);
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (errorCode == Constants.success) {
                EventBus.getDefault().post(new SendTalkEvent(0, 0, callId));
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openGroupTalk(callId);
                    }
                });
            } else {
                EventBus.getDefault().post(new SendTalkEvent(1, 0, 0));
            }
        }).start();
    }

    /**
     * 抢麦模式的发送
     * @param context    上下文
     * @param groupid    群组id
     * @param memberList 邀请的成员
     */
    public void sendRobGroupTalk(final Activity context, final int groupid, List<Member> memberList) {
        user = SpUtil.getUser();
        final byte[] bytes = new byte[1024];
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
        final String json = sendJson;
        new Thread(() -> {
            int i = WMIMSdk.getInstance().ChatStartGroup(groupid, Constants.WM_IM_CallType.WM_IM_CallType_Group_Voice_RobMic, json);
            final int callId = Constants.WM_GetCallId(i);
            int errorCode = Constants.WM_GetErrorCode(i);
            if (errorCode == Constants.WM_IM_ErrorCode.WM_IM_ErrorCode_IsCalling) {
                WMIMSdk.getInstance().ChatJoin(callId, bytes, bytes.length);
                try {
                    String group = new String(bytes, "utf-8");
                    EventBus.getDefault().post(new SendTalkEvent(0, 0, callId, group));
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            openRobGroupTalk(callId);
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (errorCode == Constants.success) {
                EventBus.getDefault().post(new ChatStartGroupSuccessEvent(callId));
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openGroupTalk(callId);
                    }
                });
            } else {
                EventBus.getDefault().post(new ChatStartGroupFailEvent(callId));
            }
        }).start();
    }

    /**
     * 强拉抢麦
     *
     * @param context    上下文
     * @param groupid    群组id
     * @param memberList 邀请的成员
     */
    public void sendTrailRob(final Activity context, final int groupid, List<Member> memberList) {
        user = SpUtil.getUser();
        final byte[] bytes = new byte[1024];
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
        final String json = sendJson;
        new Thread(() -> {
            int i = WMIMSdk.getInstance().ChatStartGroup(groupid, 14, json);
            final int callId = Constants.WM_GetCallId(i);
            int errorCode = Constants.WM_GetErrorCode(i);
            if (errorCode == Constants.WM_IM_ErrorCode.WM_IM_ErrorCode_IsCalling) {
                WMIMSdk.getInstance().ChatJoin(callId, bytes, bytes.length);
                try {
                    String group = new String(bytes, "utf-8");
                    EventBus.getDefault().post(new SendTalkEvent(0, 0, callId, group));
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            openRobGroupTalk(callId);
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (errorCode == Constants.success) {
                EventBus.getDefault().post(new SendTalkEvent(0, 0, callId));
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openGroupTalk(callId);
                    }
                });
            } else {
                EventBus.getDefault().post(new SendTalkEvent(1, 0, 0));
            }
        }).start();
    }

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

    /**
     * 静音
     *
     * @param isMute
     */
    public void muteVoice(boolean isMute) {
        rtcEngine.muteLocalAudioStream(isMute);
    }

    /**
     * 外放
     *
     * @param isSpeak
     */
    public void speakerphone(boolean isSpeak) {
        rtcEngine.setEnableSpeakerphone(isSpeak);
    }

    /**
     * 离开单人聊天
     */
    public void closeSingleTalk() {
        if (rtcEngine != null) {
            rtcEngine.disableAudio();
            rtcEngine.leaveChannel();
        }
//        if (myRunnable != null) {
//            myRunnable.stopRequest();
//            myRunnable = null;
//        }
        stopTime();
    }

    /**
     * 打开多人聊天
     */
    public void openRobGroupTalk(int roomId) {
        this.roomId = roomId;
        rtcEngine.enableAudio();
        rtcEngine.setEnableSpeakerphone(true);
        rtcEngine.muteLocalAudioStream(true);
        rtcEngine.joinChannel(null, roomId + "", null, SpUtil.getUser().getId());
        startTime();
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
        rtcEngine.setEnableSpeakerphone(true);
    }

    /**
     * 打开多人聊天
     */
    public void openGroupTalk(int roomId) {
        this.roomId = roomId;
        rtcEngine.enableAudio();
        rtcEngine.joinChannel(null, roomId + "", null, SpUtil.getUser().getId());
        rtcEngine.setEnableSpeakerphone(false);
        startTime();
    }

    /**
     * 离开多人聊天
     */
    public void closeGroupTalk() {
        if (rtcEngine != null) {
            rtcEngine.disableAudio();
            rtcEngine.leaveChannel();
        }
        stopTime();

    }

    private int talkTime = 0;

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
                    onTimeCallBack.TimeCallBack(talkTime);
                if (onWindowTimeCallBack != null)
                    onWindowTimeCallBack.TimeCallBack(talkTime);
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


//    class MyRunnable implements Runnable {
//        private volatile boolean stopRequested;
//        private Thread runThread;
//        private int talkTime = 0;
//        public void run() {
//            runThread = Thread.currentThread();
//            stopRequested = false;
//            while (!stopRequested) {
//                try {
//                    Thread.sleep(1000);
//                    talkTime = talkTime + 1;
//                    if (onTimeCallBack != null)
//                        onTimeCallBack.TimeCallBack(talkTime);
//                    if (onWindowTimeCallBack != null)
//                        onWindowTimeCallBack.TimeCallBack(talkTime);
//                } catch (InterruptedException x) {
//                    Thread.currentThread().interrupt(); // re-assert interrupt
//                }
//            }
//        }
//
//        public void stopRequest() {
//            stopRequested = true;
//            if (runThread != null) {
//                runThread.interrupt();
//            }
//        }
//    }


    private IRtcEngineEventHandler handler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            if (channel.equals(roomId + "")) {

            }
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
    OnTimeListener onTimeCallBack;
    OnTimeListener onWindowTimeCallBack;

    public void setOnTimeCallBack(OnTimeListener onTimeCallBack) {
        this.onTimeCallBack = onTimeCallBack;
    }

    public interface OnTimeListener {
        void TimeCallBack(long time);
    }

    public void setOnWindowCcallBack(OnTimeListener onWindowTimeCallBack) {
        this.onWindowTimeCallBack = onWindowTimeCallBack;
    }
}
