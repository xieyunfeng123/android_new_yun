package com.ityun.zhihuiyun.talk;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.base.BaseActivity;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.TalkRecieve;
import com.ityun.zhihuiyun.event.SendTalkBackEvent;
import com.ityun.zhihuiyun.event.SendTalkEvent;
import com.ityun.zhihuiyun.util.GroupChatUtil;
import com.ityun.zhihuiyun.util.PermissionUtil;
import com.ityun.zhihuiyun.util.screen.Eyes;
import com.ityun.zhihuiyun.view.CircleTextView;
import com.ityun.zhihuiyun.window.TalkWindowService;
import com.wm.WMIMSdk;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.Timer;
import java.util.TimerTask;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/1 0001.
 */

public class SingleTalkActivity extends BaseActivity {

    private Account account;

    private static int chatRoomId;

    @BindView(R.id.talk_img_name)
    CircleTextView talk_img_name;

    @BindView(R.id.talk_name)
    TextView talk_name;

    @BindView(R.id.talk_state)
    TextView talk_state;

    @BindView(R.id.talk_mute)
    LinearLayout talk_mute;

    @BindView(R.id.talk_refuse)
    LinearLayout talk_refuse;

    @BindView(R.id.talk_hf)
    LinearLayout talk_hf;


    @BindView(R.id.talk_accept)
    LinearLayout talk_accept;

    @BindView(R.id.to_window)
    ImageView to_window;

    @BindView(R.id.mute_img)
    ImageView mute_img;

    @BindView(R.id.hf_img)
    ImageView hf_img;

    public static boolean isFiish = true;

    private TalkRecieve recieve;

    private byte[] bytes = new byte[1024];

    public static boolean hasTalk = false;

    public static boolean isMute = false;

    public static boolean isHF = false;

    private Vibrator vibrator;

    public static SingleTalkActivity singleTalkActivity;

    public static SingleTalkActivity getInstance() {
        if (singleTalkActivity == null)
            singleTalkActivity = new SingleTalkActivity();
        return singleTalkActivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarColor(this, getResources().getColor(R.color.talk_bg));
        setContentView(R.layout.activity_accoutn_talk);
        ButterKnife.bind(this);
        vibrator = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
        //发送单人语音邀请
        account = (Account) getIntent().getSerializableExtra("account");
        //接收到单人语音邀请
        recieve = (TalkRecieve) getIntent().getSerializableExtra("receive");
        chatRoomId = getIntent().getIntExtra("chatRoomId", 0);
        GroupChatUtil.getInstance().setOnTimeCallBack(new GroupChatUtil.OnTimeCallBack() {
            @Override
            public void OnCall(int time) {
                final String nowTime = intToTime(time);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        talk_state.setText(nowTime);
                    }
                });
            }
        });
    }

    @OnClick({R.id.talk_accept, R.id.mute_img, R.id.hf_img, R.id.talk_refuse, R.id.talk_img_name, R.id.to_window})
    public void talkOnClick(View view) {
        switch (view.getId()) {
            case R.id.mute_img:
                if (isMute) {
                    mute_img.setImageResource(R.mipmap.jingyin);
                } else {
                    mute_img.setImageResource(R.mipmap.jingyin_yes);
                }
                isMute = !isMute;
                GroupChatUtil.getInstance().muteVoice(isMute);
                break;
            case R.id.hf_img:
                if (isHF) {
                    hf_img.setImageResource(R.mipmap.kuoyin);
                } else {
                    hf_img.setImageResource(R.mipmap.kuoyin_yes);
                }
                isHF = !isHF;
                GroupChatUtil.getInstance().speakerphone(isHF);
                break;
            case R.id.talk_refuse:
                vibrator.cancel();
                //拒绝
                closeActivity();
                break;
            case R.id.talk_accept:
                vibrator.cancel();
                //接受
                if (hasTalk) {
                    return;
                }
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                isMute = false;
                isHF = false;
                WMIMSdk.getInstance().ChatCalleeAck(recieve.getChatid(), recieve.getSponsorid(), 0, bytes, bytes.length);
                GroupChatUtil.getInstance().openSingleTalk(recieve.getChatid());
                to_window.setVisibility(View.VISIBLE);
                hasTalk = true;
                talk_accept.setVisibility(View.GONE);
                talk_hf.setVisibility(View.VISIBLE);
                talk_mute.setVisibility(View.VISIBLE);
                break;
            case R.id.talk_img_name:

                break;
            case R.id.to_window:
                //打开悬浮窗
                if (PermissionUtil.getAppOps(this)) {
                    if (account != null) {
                        Intent intent = new Intent(this, TalkWindowService.class);
                        intent.putExtra("chatRoomId", chatRoomId);
                        intent.putExtra("account", account);
                        startService(intent);
                    } else {
                        Intent intent = new Intent(this, TalkWindowService.class);
                        intent.putExtra("receive", recieve);
                        intent.putExtra("chatRoomId", chatRoomId);
                        startService(intent);
                    }
                    finish();
                } else {
                    Tost("没有悬浮窗口权限，请打开相关权限！");
                }
                break;
        }
    }


    private Timer timer;

    private int minTime;


    private void closeActivity() {
        if (recieve != null) {
            if (hasTalk) {
                WMIMSdk.getInstance().ChatEnd(recieve.getChatid());
            } else {
                WMIMSdk.getInstance().ChatCalleeAck(recieve.getChatid(), recieve.getSponsorid(), 1, bytes, bytes.length);
            }
            //如果不在播放就是拒绝请求 如果是在播放就算是挂断通知 反正就一个接口
        } else {
            if (hasTalk) {
                WMIMSdk.getInstance().ChatEnd(chatRoomId);
            } else {
                final String sendJson = "{\"userids\":[" + "{\"userid\":" + account.getId() + "}" + "]}";
                WMIMSdk.getInstance().ChatCancelInvite(chatRoomId, sendJson);
            }
        }
        GroupChatUtil.getInstance().closeSingleTalk();
        hasTalk = false;
        isMute = false;
        isHF = false;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        finish();
    }


    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        isFiish = true;
        chatRoomId = 0;
        super.onDestroy();

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        if (account != null) {
            //account 说明是发送单人聊天邀请
            talk_img_name.setTextString(account.getName());
            talk_name.setText(account.getName());
            if (!hasTalk) {
                GroupChatUtil.getInstance().sendSingleTalk(account);
                talk_state.setText("正在呼叫...");
            } else {
                to_window.setVisibility(View.VISIBLE);
            }
            talk_accept.setVisibility(View.GONE);
        }
        if (recieve != null) {
            chatRoomId = recieve.getChatid();
            //接到语音聊天的消息
            for (Account acc : App.accountList) {
                if (acc.getId() == recieve.getSponsorid()) {
                    account = acc;
                }
            }
            talk_img_name.setTextString(account.getName());
            talk_name.setText(account.getName());
            if (!hasTalk) {
                talk_mute.setVisibility(View.GONE);
                talk_hf.setVisibility(View.GONE);
                talk_accept.setVisibility(View.VISIBLE);
                vibrator.vibrate(new long[]{1000, 1500, 500, 1500, 500,
                        1500, 500,
                }, -1);
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        minTime++;
                        if (minTime > +10 && !hasTalk) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    closeActivity();
                                }
                            });
                        }
                    }
                }, 0, 1000);
            } else {
                talk_accept.setVisibility(View.GONE);
                to_window.setVisibility(View.VISIBLE);
            }
        }
        isFiish = false;
        if (isHF) {
            hf_img.setImageResource(R.mipmap.kuoyin_yes);
        }
        if (isMute) {
            mute_img.setImageResource(R.mipmap.jingyin_yes);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendMessageEvent(SendTalkEvent event) {
        if (event.getResult() != 0) {
            if (event.getResult() != 100) {
                if (event.getResult() == 50 && event.getChatId() == chatRoomId) {
                    GroupChatUtil.getInstance().closeSingleTalk();
                } else {
                    GroupChatUtil.getInstance().closeSingleTalk();
                }
            }
            hasTalk = false;
            isMute = false;
            isHF = false;
            finish();
        } else {
            chatRoomId = event.getChatId();
        }
    }

    public void notifyFinish(int chatid) {
        if (chatRoomId != 0 && chatRoomId == chatid) {
            GroupChatUtil.getInstance().closeSingleTalk();
            hasTalk = false;
            isMute = false;
            isHF = false;
            EventBus.getDefault().post(new SendTalkEvent(10, 0, chatRoomId));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 发送的邀请得到回应
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recieveTalkBackEvent(SendTalkBackEvent event) {
        if (event.getResponse() == 1) {
            if (hasTalk) {
                //正在通话过程中接受到挂断请求
                GroupChatUtil.getInstance().closeSingleTalk();
                hasTalk = false;
                isMute = false;
                isHF = false;
                Tost("结束语音对讲");
            } else {
                if (recieve != null) {
                    Tost("对方已挂断");
                } else {
                    Tost("对方正忙");
                }
            }
            finish();
        } else {
            if (event.getChatid() == chatRoomId) {
                to_window.setVisibility(View.VISIBLE);
                GroupChatUtil.getInstance().openSingleTalk(chatRoomId);
                hasTalk = true;
                isMute = false;
                isHF = false;
            }
        }
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
