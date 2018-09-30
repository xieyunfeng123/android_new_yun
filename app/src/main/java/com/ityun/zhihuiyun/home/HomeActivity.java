package com.ityun.zhihuiyun.home;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.base.UserpresentReceiver;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.AccountIM;
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.bean.HomeMessage;
import com.ityun.zhihuiyun.bean.IMMessage;
import com.ityun.zhihuiyun.bean.IntercomBean;
import com.ityun.zhihuiyun.bean.Member;
import com.ityun.zhihuiyun.bean.MessageGroupCallBean;
import com.ityun.zhihuiyun.bean.MessageSingleCallBean;
import com.ityun.zhihuiyun.bean.RequstGroup;
import com.ityun.zhihuiyun.bean.SyncGroupBean;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.bean.TalkRecieve;
import com.ityun.zhihuiyun.chat.ChatActivity;
import com.ityun.zhihuiyun.db.HomeMessageUtil;
import com.ityun.zhihuiyun.db.IMUtil;
import com.ityun.zhihuiyun.event.FinishEvent;
import com.ityun.zhihuiyun.event.FreeRobEvent;
import com.ityun.zhihuiyun.event.GroupEvent;
import com.ityun.zhihuiyun.event.HomeEvent;
import com.ityun.zhihuiyun.event.ReceiveMessageEvent;
import com.ityun.zhihuiyun.event.RequestRobEvent;
import com.ityun.zhihuiyun.event.SendTalkBackEvent;
import com.ityun.zhihuiyun.event.SendTalkEvent;
import com.ityun.zhihuiyun.event.UserOnLineEvent;
import com.ityun.zhihuiyun.fragment.MeFragment;
import com.ityun.zhihuiyun.fragment.MessageFragment;
import com.ityun.zhihuiyun.fragment.PhoneBookFragment;
import com.ityun.zhihuiyun.locate.BaiduMapUtil;
import com.ityun.zhihuiyun.talk.GroupTalkActivity;
import com.ityun.zhihuiyun.talk.RobTalkActivity;
import com.ityun.zhihuiyun.talk.SingleTalkActivity;
import com.ityun.zhihuiyun.talk.StrongPullTalkActivity;
import com.ityun.zhihuiyun.talk.TalkUtil;
import com.ityun.zhihuiyun.talk.TrailRobActivity;
import com.ityun.zhihuiyun.teamviewer.SendVideoActivity;
import com.ityun.zhihuiyun.util.FileUtil;
import com.ityun.zhihuiyun.util.GroupChatUtil;
import com.ityun.zhihuiyun.util.SpUtil;
import com.ityun.zhihuiyun.util.screen.AppFileUtil;
import com.ityun.zhihuiyun.util.screen.Eyes;
import com.ityun.zhihuiyun.window.TalkWindowService;
import com.noober.menu.FloatMenu;
import com.vomont.fileloadsdk.WMFileLoadSdk;
import com.wm.Constants;
import com.wm.WMIMSdk;
import com.zhy.clientsdk.ZHYClientSdk;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2018/5/21 0021.
 */

public class HomeActivity extends BaseActivity implements MessageFragment.OnListFragmentInteractionListener {

    /* 消息 */
    @BindView(R.id.home_message)
    RadioButton message;

    /* 通讯录 */
    @BindView(R.id.home_phoneBook)
    RadioButton phoneBook;

    /* 我的*/
    @BindView(R.id.home_me)
    RadioButton me;

    @BindView(R.id.activity_home_frame)
    FrameLayout activity_home_frame;

    private MeFragment meFragment;

    private MessageFragment messageFragment;

    private PhoneBookFragment phoneBookFragment;

    private Fragment nowFragment;

    private Handler handler, mhandler;

    SysConfBean bean;

    public static List<GroupInfo> groupInfos = new ArrayList<>();

    private byte[] bytes;

    private List<IMMessage> messageList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarColor(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_home);
        GroupTalkActivity.getInstance();
        RobTalkActivity.getInstance();
        StrongPullTalkActivity.getInstance();
        SendVideoActivity.getInstance();
        TrailRobActivity.getInstance();
        ButterKnife.bind(this);

        initFragment();
        initHandler();
        screenListener();
        if (savedInstanceState != null) {
            App.getInstance().setSysConfBean((SysConfBean) savedInstanceState.getSerializable("bean"));
        }
        loginIM();
        BaiduMapUtil.getInstance().init(this);
    }


    private void screenListener() {
        IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // 屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT);
        // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
        // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
        // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(new UserpresentReceiver(), filter);
    }

    /**
     * 初始化fragment
     */
    private void initFragment() {
        messageFragment = new MessageFragment();
        phoneBookFragment = new PhoneBookFragment();
        meFragment = new MeFragment();
        nowFragment = messageFragment;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.activity_home_frame, messageFragment).commit();
    }

    private void changeFragment(Fragment fromFragment, Fragment toFragment) {
        if (nowFragment == null || nowFragment != toFragment) {
            nowFragment = toFragment;
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (toFragment.isAdded() == false) {
            ft.hide(fromFragment).add(R.id.activity_home_frame, toFragment).commit();
        } else {
            ft.hide(fromFragment).show(toFragment).commit();
        }
    }


    @OnClick({R.id.home_message, R.id.home_phoneBook, R.id.home_me})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_message:
                changeFragment(nowFragment, messageFragment);
                nowFragment = messageFragment;
                break;
            case R.id.home_phoneBook:
                changeFragment(nowFragment, phoneBookFragment);
                nowFragment = phoneBookFragment;
                break;
            case R.id.home_me:
                changeFragment(nowFragment, meFragment);
                nowFragment = meFragment;
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ZHYClientSdk.getInstance().setHandler(handler);
    }

    @SuppressLint("HandlerLeak")
    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private Point point = new Point();

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            point.x = (int) ev.getRawX();
            point.y = (int) ev.getRawY();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onListFragmentInteraction(final HomeMessage message) {
        FloatMenu floatMenu = new FloatMenu(HomeActivity.this);
        floatMenu.items("删除");
        floatMenu.setOnItemClickListener((v, position) -> {
            HomeMessageUtil.getInstance().deleteMessage(message);
            IMUtil.getInstance().deleteDB(bean.getAccountid(), message.getId());
            EventBus.getDefault().post(new HomeEvent());
        });
        floatMenu.show(point);
    }

    private Map<Integer, String> map = new HashMap<>();

    public void loginIM() {
        bytes = new byte[1024];
        bean = App.getInstance().getSysConfBean();
        SpUtil.upDataId(bean.getAccountid());
        vibrator = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
        //声网的初始化
        TalkUtil.getInstance().init();
        if (bean != null && bean.getImsvrip() != null && bean.getImsvrport() != 0) {
            WMIMSdk.getInstance().Init(bean.getImsvrip(), bean.getImsvrport(), 63, 3);
        }
        WMFileLoadSdk.getInstance().WM_VFile_Init(bean.getVfilesvrip(), bean.getVfilesvrhttpport(), bean.getAccountid(), "", this);
        WMFileLoadSdk.getInstance().setEventBigFileCallback(new WMFileLoadSdk.EventBigFileCallback() {
            @Override
            public void OnSucess(int i, String s) {
                handler.postDelayed(() -> {
                    if (map.get(i) != null) {
                        return;
                    } else {
                        map.put(i, s);
                    }
                    IMMessage message = IMUtil.getInstance().selectMessageByLoadId(bean.getAccountid(), i);
                    if (message != null) {
                        message.setFileUrl(s);
                        IMUtil.getInstance().upDateMessageByLoadId(message);
                        if (s.contains("mp4")) {
                            sendMessage(3, 0, message);
                        } else {
                            sendMessage(2, 0, message);
                        }
                    }
                }, 300);
            }

            @Override
            public void OnFail(int i, int i1) {
                handler.postDelayed(() -> {
                    IMMessage message = IMUtil.getInstance().selectMessageByLoadId(bean.getAccountid(), i);
                    if (message != null) {
                        message.setSendState(2);
                        IMUtil.getInstance().upDateMessageByLoadId(message);
                    }
                    EventBus.getDefault().post(new ReceiveMessageEvent(null));
                }, 1000);
            }

            @Override
            public void OnProgress(int i, int i1) {
                handler.postDelayed(() -> {
                    IMMessage message = IMUtil.getInstance().selectMessageByLoadId(bean.getAccountid(), i);
                    if (message != null) {
                        message.setFileProgress(i1);
                        IMUtil.getInstance().upDateMessageByLoadId(message);
                    }
                }, 300);
            }
        });

        SpUtil.upDataId(bean.getAccountid());
        AppFileUtil.getInstance().init(SpUtil.getUser());
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String mImei = "a_" + TelephonyMgr.getDeviceId();
        WMIMSdk.getInstance().Register(bean.getAccountid(), bean.getSignature(), mImei,
                mySvrMsgCallBack, groupMsgCallBack, registerMsgCallBack, callMsgCallBack, chatMsgCallBack);
    }

    private WMIMSdk.OnSvrMsgCallBackListen mySvrMsgCallBack = (nMsgId, jsonStr, jsonStrLen) -> {
        switch (nMsgId) {
            case Constants.SvrMsgId.WM_IM_SvrMsgId_UserOnline:
                // 上线
                updataUserState(jsonStr, Constants.SvrMsgId.WM_IM_SvrMsgId_UserOnline);
                break;
            case Constants.SvrMsgId.WM_IM_SvrMsgId_UserOffline:
                // 下线
                updataUserState(jsonStr, Constants.SvrMsgId.WM_IM_SvrMsgId_UserOffline);
                break;
            default:
                break;
        }
    };

    private WMIMSdk.OnGroupMsgCallBackListen groupMsgCallBack = (nMsgId, jsonStr, jsonStrLen) -> {
        Gson gson = new Gson();
        RequstGroup requstGroup = gson.fromJson(jsonStr, RequstGroup.class);
        switch (nMsgId) {
            case Constants.GroupMsgId.WM_IM_GroupMsgId_Join_Notify:
                // 被邀请入群通知
                groupInfos.add(requstGroup.getGroupinfo());
                EventBus.getDefault().post(new UserOnLineEvent());
                EventBus.getDefault().post(new GroupEvent());
                break;
            case Constants.GroupMsgId.WM_IM_GroupMsgId_Other_Join_Notify:
                // 其他人加群通知, {"sponsorid", "groupid", "joinids":[{"id"}]}
                for (GroupInfo groupInfo : groupInfos) {
                    if (groupInfo.getId() == requstGroup.getGroupid()) {
                        groupInfo.getMember().addAll(requstGroup.getJoinids());
                    }
                }
                break;
            case Constants.GroupMsgId.WM_IM_GroupMsgId_KickOut_Notify:
                // 被踢通知(自己), {"sponsorid", "groupid"}
                for (GroupInfo groupInfo : groupInfos) {
                    if (groupInfo.getId() == requstGroup.getGroupid()) {
                        groupInfos.remove(groupInfo);
                        break;
                    }
                }
                IMUtil.getInstance().deleteDB(App.getInstance().getSysConfBean().getAccountid(), requstGroup.getGroupid());
                HomeMessage homeMessage1 = HomeMessageUtil.getInstance().selectMessageById(App.getInstance().getSysConfBean().getAccountid(), requstGroup.getGroupid());
                HomeMessageUtil.getInstance().deleteMessage(homeMessage1);
                EventBus.getDefault().post(new HomeEvent());
                EventBus.getDefault().post(new GroupEvent());
                EventBus.getDefault().post(new FinishEvent("ChatActivity", requstGroup.getGroupid()));
                break;
            case Constants.GroupMsgId.WM_IM_GroupMsgId_Other_KickOut_Notify:
                //被踢通知(其他), {"sponsorid", "groupid", "kickoutid"}
                for (GroupInfo groupInfo : groupInfos) {
                    if (groupInfo.getId() == requstGroup.getGroupid()) {
                        if (groupInfo.getMember() != null) {
                            for (Member member : groupInfo.getMember()) {
                                if (member.getId() == requstGroup.getKickoutid()) {
                                    groupInfo.getMember().remove(member);
                                    break;
                                }
                            }
                        }
                    }
                }

                break;
            case Constants.GroupMsgId.WM_IM_GroupMsgId_HadLeft_Notify:
                //退群通知, {"groupid", "userid"}
                for (GroupInfo groupInfo : groupInfos) {
                    if (groupInfo.getId() == requstGroup.getGroupid()) {
                        if (groupInfo.getMember() != null) {
                            for (Member member : groupInfo.getMember()) {
                                if (member.getId() == requstGroup.getUserid()) {
                                    groupInfo.getMember().remove(member);
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
            case Constants.GroupMsgId.WM_IM_GroupMsgId_Dissolve_Notify:
                //解散通知, {"sponsorid", "groupid"}
                for (GroupInfo groupInfo : groupInfos) {
                    if (groupInfo.getId() == requstGroup.getGroupid()) {
                        groupInfos.remove(groupInfo);
                        break;
                    }
                }
                IMUtil.getInstance().deleteDB(App.getInstance().getSysConfBean().getAccountid(), requstGroup.getGroupid());
                HomeMessage hMessage = HomeMessageUtil.getInstance().selectMessageById(App.getInstance().getSysConfBean().getAccountid(), requstGroup.getGroupid());
                HomeMessageUtil.getInstance().deleteMessage(hMessage);
                EventBus.getDefault().post(new HomeEvent());
                EventBus.getDefault().post(new GroupEvent());
                EventBus.getDefault().post(new FinishEvent("ChatActivity", requstGroup.getGroupid()));
                break;
            default:
                break;
        }
    };


    private WMIMSdk.OnRegisterMsgCallBackListen registerMsgCallBack = (jsonStr, nMsgId) -> {
        Log.e("insert", "=OnRegisterMsgCallBackListen=" + jsonStr);
        //登录成功后 返回群组
        Gson gson = new Gson();
        SyncGroupBean syncGroupBean = gson.fromJson(jsonStr, SyncGroupBean.class);
        if (syncGroupBean != null && syncGroupBean.getSyncgroup() != null) {
            //添加组
            groupInfos.clear();
            groupInfos.addAll(syncGroupBean.getSyncgroup());
            if (syncGroupBean.getSyncuser() != null) {
                //修改好友的状态
                for (AccountIM accountIM : syncGroupBean.getSyncuser()) {
                    for (Account account : App.accountList) {
                        if (account.getId() == accountIM.getId()) {
                            account.setStatus(1);
                        }
                    }
                }
            }
            EventBus.getDefault().post(new UserOnLineEvent());
            EventBus.getDefault().post(new GroupEvent());
        }
    };

    private WMIMSdk.OnCallMsgCallBackListen callMsgCallBack = (i, s, i1) -> {
        Gson gson;
        switch (i) {
            case Constants.CallMsgId.WM_IM_CallMsgId_Invite_Notify:
                //接收到语音聊天的请求
                gson = new Gson();
                TalkRecieve recieve = gson.fromJson(s, TalkRecieve.class);
                receiveTalkOrVideo(recieve);
                break;
            case Constants.CallMsgId.WM_IM_CallMsgId_Invite_Response:
                //邀请聊天返回的值
                // {"chatid" : 130,"response" : 0,"userid" : 185} response 0同意 1拒绝
                gson = new Gson();
                SendTalkBackEvent sendTalkBackEvent = gson.fromJson(s, SendTalkBackEvent.class);
                if (!SingleTalkActivity.isFiish) {
                    EventBus.getDefault().post(sendTalkBackEvent);
                }
                Member member = new Member();
                member.setId(sendTalkBackEvent.getUserid());
                if (sendTalkBackEvent.getResponse() == 0) {
                    GroupChatUtil.getInstance().addMember(member);
                } else {
                    GroupChatUtil.getInstance().lostMember(member);
                }
                break;
            case Constants.CallMsgId.WM_IM_CallMsgId_CancelInvite_Notify:
                //取消邀请的通知
                if (!SingleTalkActivity.isFiish) {
                    gson = new Gson();
                    SendTalkBackEvent json = gson.fromJson(s, SendTalkBackEvent.class);
                    EventBus.getDefault().post(new SendTalkEvent(100, 0, json.getChatid()));
                }
                break;
            case Constants.CallMsgId.WM_IM_CallMsgId_MemLost_Notify:
                // 成员退出
                if (SingleTalkActivity.hasTalk) {
                    //单人聊天的时候 退出就是结束
                    gson = new Gson();
                    SendTalkBackEvent json = gson.fromJson(s, SendTalkBackEvent.class);
                    if (!SingleTalkActivity.isFiish) {
                        runOnUiThread(() -> SingleTalkActivity.getInstance().notifyFinish(json.getChatid()));
                    } else {
                        TalkUtil.getInstance().closeSingleTalk();
                        Intent intent = new Intent(HomeActivity.this, TalkWindowService.class);
                        stopService(intent);
                        SingleTalkActivity.hasTalk = false;
                        SingleTalkActivity.isMute = false;
                        SingleTalkActivity.isHF = false;
                    }
                } else {
                    if (GroupChatUtil.getInstance().isCalling()) {
                        gson = new Gson();
                        SendTalkBackEvent json = gson.fromJson(s, SendTalkBackEvent.class);
                        Member member1 = new Member();
                        member1.setId(json.getUserid());
                        GroupChatUtil.getInstance().lostMember(member1);
                    }
                }
                break;
            case Constants.CallMsgId.WM_IM_CallMsgId_MemAdd_Notify:
                if (GroupChatUtil.getInstance().isCalling()) {
                    Gson gson1 = new Gson();
                    GroupEvent json = gson1.fromJson(s, GroupEvent.class);
                    Member member2 = new Member();
                    member2.setId(json.getUserid());
                    GroupChatUtil.getInstance().addMember(member2);
                }
                break;
            case Constants.CallMsgId.WM_IM_CallMsgId_RobMic_Notify:
                //接收到抢麦通知
                gson = new Gson();
                JsonReader jsonReaders = new JsonReader(new StringReader(s));//其中jsonContext为String类型的Json数据
                jsonReaders.setLenient(true);
                final IntercomBean intercomBean = gson.fromJson(jsonReaders, IntercomBean.class);
//                runOnUiThread(() -> RobTalkActivity.requestRob(intercomBean));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new RequestRobEvent(intercomBean));
                    }
                });
                break;
            case Constants.CallMsgId.WM_IM_CallMsgId_FreeMic_Notify:
                //接收到放麦通知
                gson = new Gson();
                JsonReader jsonReader = new JsonReader(new StringReader(s));//其中jsonContext为String类型的Json数据
                jsonReader.setLenient(true);
                final IntercomBean bean = gson.fromJson(jsonReader, IntercomBean.class);
//                runOnUiThread(() -> RobTalkActivity.freeRob(bean));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new FreeRobEvent(bean));
                    }
                });
                break;
            default:
                break;
        }
    };

    private Vibrator vibrator;
    Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    Ringtone rt;
    private WMIMSdk.OnChatMsgCallBackListen chatMsgCallBack = new WMIMSdk.OnChatMsgCallBackListen() {
        @Override
        public void OnChatMsgCallBack(int i, int i1, String s, int i2) {
            if (ChatActivity.isFiish) {
                if (SpUtil.getShock(HomeActivity.this) == 1) {
                    vibrator.cancel();
                    vibrator.vibrate(new long[]{1000, 2000}, -1);
                }
                if (SpUtil.getVoice(HomeActivity.this) == 1) {
                    // 声音
                    if (rt != null) {
                        rt.stop();
                        rt = null;
                    }
                    rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
                    rt.play();
                }
            }
            Gson gson;
            switch (i) {
                case Constants.ChatMsgTypeId.WM_IM_ChatMsgTypeId_Single:
                    //单人聊天接受到的消息
                    gson = new Gson();
                    MessageSingleCallBean messageCallBean = gson.fromJson(s, MessageSingleCallBean.class);
                    receiveIMSingle(messageCallBean);
                    break;
                case Constants.ChatMsgTypeId.WM_IM_ChatMsgTypeId_Group:
                    //群组聊天接受的消息
                    gson = new Gson();
                    MessageGroupCallBean groupCallBean = gson.fromJson(s, MessageGroupCallBean.class);
                    receiveIMGroup(groupCallBean);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 接受到单聊的IM消息
     *
     * @param messageCallBean
     */
    private void receiveIMSingle(MessageSingleCallBean messageCallBean) {
        if (messageCallBean != null) {
            switch (messageCallBean.getType()) {
                case Constants.WM_IM_MsgType.WM_IM_MsgType_Text:
                    //接收到单人文本消息
                    addSingleMessage(messageCallBean.getSrcuserId(), 0, messageCallBean.getContent(), (long) messageCallBean.getSendtime() * 1000);
                    break;
                case Constants.WM_IM_MsgType.WM_IM_MsgType_Voice:
                    //接受到单人语音消息
                    addSingleMessage(messageCallBean.getSrcuserId(), 1, messageCallBean.getContent(), (long) messageCallBean.getSendtime() * 1000);
                    break;
                case Constants.WM_IM_MsgType.WM_IM_MsgType_Pic:
                    // 接收图片
                    addSingleMessage(messageCallBean.getSrcuserId(), 2, messageCallBean.getContent(), (long) messageCallBean.getSendtime() * 1000);
                    break;
                case Constants.WM_IM_MsgType.WM_IM_MsgType_Video:
                    // 接收视频
                    addSingleMessage(messageCallBean.getSrcuserId(), 3, messageCallBean.getContent(), (long) messageCallBean.getSendtime() * 1000);
                    break;
                case Constants.WM_IM_MsgType.WM_IM_MsgType_Location:
                    // 接收位置
                    addSingleMessage(messageCallBean.getSrcuserId(), 4, messageCallBean.getContent(), (long) messageCallBean.getSendtime() * 1000);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 接收到群聊的IM消息
     *
     * @param groupCallBean
     */
    private void receiveIMGroup(MessageGroupCallBean groupCallBean) {
        if (groupCallBean != null) {
            switch (groupCallBean.getType()) {
                case Constants.WM_IM_MsgType.WM_IM_MsgType_Text:
                    //接收到多人文本消息
                    addGroupMessage(groupCallBean.getGroupid(), 0, groupCallBean.getContent(), groupCallBean.getSrcuserId(), (long) groupCallBean.getSendtime() * 1000);
                    break;
                case Constants.WM_IM_MsgType.WM_IM_MsgType_Voice:
                    //接受多人语音消息
                    addGroupMessage(groupCallBean.getGroupid(), 1, groupCallBean.getContent(), groupCallBean.getSrcuserId(), (long) groupCallBean.getSendtime() * 1000);
                    break;
                case Constants.WM_IM_MsgType.WM_IM_MsgType_Pic:
                    //接受到图片消息
                    addGroupMessage(groupCallBean.getGroupid(), 2, groupCallBean.getContent(), groupCallBean.getSrcuserId(), (long) groupCallBean.getSendtime() * 1000);
                    break;
                case Constants.WM_IM_MsgType.WM_IM_MsgType_Video:
                    //接受到视频消息
                    addGroupMessage(groupCallBean.getGroupid(), 3, groupCallBean.getContent(), groupCallBean.getSrcuserId(), (long) groupCallBean.getSendtime() * 1000);
                    break;
                case Constants.WM_IM_MsgType.WM_IM_MsgType_Location:
                    //接收位置信息
                    addGroupMessage(groupCallBean.getGroupid(), 4, groupCallBean.getContent(), groupCallBean.getSrcuserId(), (long) groupCallBean.getSendtime() * 1000);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 接受到语音聊天或者视频聊天的消息
     *
     * @param recieve
     */
    private void receiveTalkOrVideo(TalkRecieve recieve) {
                
        int chatType = Constants.WM_GetCallType(recieve.getChattype());
        if (bean.getAccountid() == recieve.getSponsorid()) {
            return;
        }
        if (GroupChatUtil.getInstance().isCalling()) {
            //如果正在聊天界面或正在聊天 说明在忙 拒绝邀请
            WMIMSdk.getInstance().ChatCalleeAck(recieve.getChatid(), recieve.getSponsorid(), 1, bytes, bytes.length);
            return;
        }
        switch (chatType) {
            case Constants.WM_IM_CallType.WM_IM_CallType_Single_Voice:
                //接收到单人语音聊天的消息
                Intent intent = new Intent(HomeActivity.this, SingleTalkActivity.class);
                intent.putExtra("receive", recieve);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case Constants.WM_IM_CallType.WM_IM_CallType_Group_Voice:
                //接收到多人语音的消息
                Intent groupintent = new Intent(HomeActivity.this, GroupTalkActivity.class);
                groupintent.putExtra("receive", recieve);
                startActivity(groupintent);
                break;
            case Constants.WM_IM_CallType.WM_IM_CallType_Group_Voice_RobMic:
                //接到需要抢麦的多人语音聊天消息的邀请
                Intent grouprobintent = new Intent(HomeActivity.this, RobTalkActivity.class);
                grouprobintent.putExtra("receive", recieve);
                grouprobintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(grouprobintent);
                break;
            case 12:
                // 接收到远程协助语音聊天消息的邀请
                Intent teamViewerIntent = new Intent(HomeActivity.this, SendVideoActivity.class);
                teamViewerIntent.putExtra("receive", recieve);
                teamViewerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(teamViewerIntent);
                break;
            case 13:
                //接收到强拉消息
                Intent intentStrong = new Intent(HomeActivity.this, StrongPullTalkActivity.class);
                intentStrong.putExtra("receive", recieve);
                intentStrong.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentStrong);
                break;
            case 14:
                // 接收到强拉抢麦消息
                Intent intentTrail = new Intent(HomeActivity.this, TrailRobActivity.class);
                intentTrail.putExtra("receive", recieve);
                intentTrail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentTrail);
                break;
        }
    }

    /**
     * 个人消息存到数据库中
     *
     * @param imId 发送者id
     * @param type 发送消息的类型
     * @param str  发送的内容
     */
    private void addSingleMessage(int imId, int type, String str, long time) {
        new Thread(() -> {
            String receiveText = "";
            try {
                receiveText = java.net.URLDecoder.decode(str, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            HomeMessage homeMessage = new HomeMessage();
            homeMessage.setUsrId(bean.getAccountid());
            homeMessage.setUserName(SpUtil.getUser().getUserName());
            homeMessage.setImType(0);
            homeMessage.setTime(time);
            homeMessage.setMessageType(type);
            homeMessage.setId(imId);
            IMMessage message = new IMMessage();
            message.setUserId(bean.getAccountid());
            message.setImType(0);
            message.setImId(imId);
            String name = "";
            Account notifAccount = null;
            for (Account account : App.accountList) {
                if (account.getId() == imId) {
                    name = account.getName();
                    notifAccount = account;
                }
            }
            message.setCreateTime(time);
            homeMessage.setName(name);
            message.setMemberName(name);
            message.setIsSelf(1);
            message.setMeessageTpye(type);
            if (type == 0) {
                //文字聊天
                homeMessage.setContent(receiveText);
                message.setMessage(receiveText);
                showSingleNotification(receiveText, notifAccount);
            } else if (type == 1) {
                //语音聊天
                byte[] newFile = Base64.decode(receiveText, Base64.DEFAULT);
                FileUtil.byte2File(newFile, AppFileUtil.getInstance().getVoicePath(), time + ".amr");
                message.setVoicePath(AppFileUtil.getInstance().getVoicePath() + "/" + time + ".amr");
                showSingleNotification("[语音]", notifAccount);
            } else if (type == 2) {
                message.setFileUrl(receiveText);
                showSingleNotification("[图片]", notifAccount);
            } else if (type == 3) {
                message.setFileUrl(receiveText);
                showSingleNotification("[视频]", notifAccount);
            } else if (type == 4) {
                homeMessage.setContent(receiveText);
                message.setMessage(receiveText);
                showSingleNotification("[位置]", notifAccount);
                showSingleNotification(receiveText, notifAccount);
            } else if (type == 3) {
                message.setFileUrl(receiveText);
                showSingleNotification(receiveText, notifAccount);
            }
            message.setIsRead(0);
            if (!TextUtils.isEmpty(name)) {
                HomeMessageUtil.getInstance().insertMessage(homeMessage);
                IMUtil.getInstance().insertMessage(message);
                EventBus.getDefault().post(new ReceiveMessageEvent(null));
                EventBus.getDefault().post(new HomeEvent());
            }
        }).start();
    }

    private static final int NO_1 = 0x1;

    /**
     * 单人聊天的消息通知
     *
     * @param message
     * @param account
     */
    public void showSingleNotification(String message, Account account) {
        if (account == null)
            return;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        int shockSp = SpUtil.getShock(this);
        int voiceSp = SpUtil.getVoice(this);
        if (shockSp == 1 && voiceSp == 1) {
            builder.setDefaults(Notification.DEFAULT_ALL);
        } else if (shockSp == 1) {
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
        } else if (voiceSp == 1) {
            builder.setDefaults(Notification.DEFAULT_SOUND);
        }
        builder.setContentTitle(account.getName());
        builder.setContentText(message);
        //设置点击通知跳转页面后，通知消失
        builder.setAutoCancel(true);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("type", 0);
        intent.putExtra("account", account);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pi);
        Notification notification = builder.build();
        List<IMMessage> messageList = IMUtil.getInstance().selectAllMeaageByRead(bean.getAccountid(), 0);
        if (messageList != null && messageList.size() != 0) {
            notification.number = messageList.size();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NO_1, notification);
    }

    /**
     * 添加群组聊天的消息进数据库
     *
     * @param imId          发送群组的id
     * @param type          消息类型
     * @param str           消息
     * @param messageSendId 发送者的id
     */
    private void addGroupMessage(int imId, int type, String str, int messageSendId, long time) {
        String receiveText = "";
        try {
            receiveText = java.net.URLDecoder.decode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HomeMessage homeMessage = new HomeMessage();
        homeMessage.setUsrId(bean.getAccountid());
        homeMessage.setImType(1);
        homeMessage.setTime(time);
        homeMessage.setMessageType(type);
        homeMessage.setId(imId);
        homeMessage.setUserName(SpUtil.getUser().getUserName());

        IMMessage message = new IMMessage();
        message.setUserId(bean.getAccountid());
        message.setImType(1);
        message.setImId(imId);
        String name = "";
        GroupInfo notifGroupinfo = null;
        if (groupInfos != null) {
            for (GroupInfo groupInfo : groupInfos) {
                if (groupInfo.getId() == imId) {
                    name = groupInfo.getName();
                    notifGroupinfo = groupInfo;
                }
            }
        }
        String groupName = "";
        try {
            groupName = URLDecoder.decode(name, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        homeMessage.setName(groupName);
        message.setSendName(groupName);
        String memberName = "";
        for (Account account : App.accountList) {
            if (account.getId() == messageSendId) {
                memberName = account.getName();
            }
        }

        message.setMessageSendId(messageSendId);
        message.setMemberName(memberName);
        message.setIsSelf(1);
        message.setMeessageTpye(type);
        message.setCreateTime(time);
        if (type == 0) {
            homeMessage.setContent(receiveText);
            message.setMessage(receiveText);
            showGroupNotification(receiveText, notifGroupinfo);
        } else if (type == 1) {
            byte[] newFile = Base64.decode(receiveText, Base64.DEFAULT);
            FileUtil.byte2File(newFile, AppFileUtil.getInstance().getVoicePath(), time + ".amr");
            message.setVoicePath(AppFileUtil.getInstance().getVoicePath() + "/" + time + ".amr");
            showGroupNotification("[语音]", notifGroupinfo);
        } else if (type == 2) {
            message.setFileUrl(receiveText);
            showGroupNotification("[图片]", notifGroupinfo);
        } else if (type == 3) {
            message.setFileUrl(receiveText);
            showGroupNotification("[视频]", notifGroupinfo);
        } else if (type == 4) {
            homeMessage.setContent(receiveText);
            message.setMessage(receiveText);
            showGroupNotification("[位置]", notifGroupinfo);
        }
        message.setIsRead(0);
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(memberName)) {
            HomeMessageUtil.getInstance().insertMessage(homeMessage);
            IMUtil.getInstance().insertMessage(message);
            EventBus.getDefault().post(new ReceiveMessageEvent(message));
            EventBus.getDefault().post(new HomeEvent());
        }
    }


    /**
     * 多人聊天的消息通知
     *
     * @param message
     * @param
     */
    public void showGroupNotification(String message, GroupInfo groupInfo) {
        if (groupInfo == null)
            return;
        String groupName = "";
        try {
            groupName = URLDecoder.decode(groupInfo.getName(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        int shockSp = SpUtil.getShock(this);
        int voiceSp = SpUtil.getVoice(this);
        if (shockSp == 1 && voiceSp == 1) {
            builder.setDefaults(Notification.DEFAULT_ALL);
        } else if (shockSp == 1) {
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
        } else if (voiceSp == 1) {
            builder.setDefaults(Notification.DEFAULT_SOUND);
        }
        builder.setContentTitle(groupName);
        builder.setContentText(message);
        //设置点击通知跳转页面后，通知消失
        builder.setAutoCancel(true);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("type", 1);
        intent.putExtra("groupinfo", groupInfo);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pi);
        Notification notification = builder.build();
//        List<IMMessage> messageList = IMUtil.getInstance().selectAllMeaageByRead(bean.getAccountid(), 0);
//        if (messageList != null && messageList.size() != 0) {
//            notification.number = messageList.size();
//        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NO_1, notification);

//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setContentTitle(groupName);
//        builder.setContentText(message);
//        //设置点击通知跳转页面后，通知消失
//        builder.setAutoCancel(true);
//        Intent intent = new Intent(this, ChatActivity.class);
//        intent.putExtra("type", 1);
//        intent.putExtra("groupinfo", groupInfo);
//        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pi);
//        Notification notification = builder.build();
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(NO_1, notification);
    }


    /**
     * 更新用户状态
     *
     * @param json
     * @param state
     */
    private void updataUserState(String json, int state) {
        if (TextUtils.isEmpty(json) && App.accountList.size() == 0) {
            return;
        }
        JSONObject object;
        try {
            object = new JSONObject(json);
            int id = object.getInt("userid");
            for (int i = 0; i < App.accountList.size(); i++) {
                if (id == App.accountList.get(i).getId()) {
                    App.accountList.get(i).setStatus(state);
                }
            }
            PhoneBookFragment.upDataOnline();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }


    /**
     * 发送消息
     *
     * @param messageType 消息的类型  2-图片，3-视频，4-位置
     * @param isResend    是否是重复发送 0不是 1是的
     */
    private void sendMessage(final int messageType, int isResend, final IMMessage message) {
        String sendContent = message.getFileUrl();
        String url = "http://" + bean.getImsvrip() + ":" + bean.getImsvrhttpport() + "/?";
        int userid = bean.getAccountid();
        String sendType = (message.getImType() == 0) ? "dstuserid" : "groupid";
        String msgid = (message.getImType() == 0) ? "257" : "258";
        int dstuserid = message.getImId();
        OkHttpUtils
                .post()
                .url(url)
                .addParams("msgid", msgid)
                .addParams("userid", userid + "")
                .addParams(sendType, dstuserid + "")
                .addParams("type", messageType + "")
                .addParams("seq", message.getCreateTime() + "")
                .addParams("content", sendContent)
                .addParams("resend", isResend + "")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                IMUtil.getInstance().upDataSendState(bean.getAccountid(), message.getCreateTime(), 2);
                EventBus.getDefault().post(new ReceiveMessageEvent(null));
            }

            @Override
            public void onResponse(String s, int i) {
                try {
                    JSONObject object = new JSONObject(s);
                    int result = object.getInt("result");
                    if (result == 0) {
                        IMUtil.getInstance().upDataSendState(bean.getAccountid(), message.getCreateTime(), 0);
                    } else {
                        IMUtil.getInstance().upDataSendState(bean.getAccountid(), message.getCreateTime(), 2);
                        Tost("发送失败");
                    }
                    EventBus.getDefault().post(new ReceiveMessageEvent(null));
                } catch (JSONException e) {
                    IMUtil.getInstance().upDataSendState(bean.getAccountid(), message.getCreateTime(), 2);
                    EventBus.getDefault().post(new ReceiveMessageEvent(null));
                }
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("bean", bean);
    }

}
