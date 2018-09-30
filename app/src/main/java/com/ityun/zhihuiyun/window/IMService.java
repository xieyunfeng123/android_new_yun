package com.ityun.zhihuiyun.window;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.ityun.zhihuiyun.R;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.AccountIM;
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.bean.HomeMessage;
import com.ityun.zhihuiyun.bean.IMMessage;
import com.ityun.zhihuiyun.bean.MessageGroupCallBean;
import com.ityun.zhihuiyun.bean.MessageSingleCallBean;
import com.ityun.zhihuiyun.bean.RequstGroup;
import com.ityun.zhihuiyun.bean.SyncGroupBean;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.bean.TalkRecieve;
import com.ityun.zhihuiyun.chat.ChatActivity;
import com.ityun.zhihuiyun.dao.IMMessageDao;
import com.ityun.zhihuiyun.db.HomeMessageUtil;
import com.ityun.zhihuiyun.db.IMUtil;
import com.ityun.zhihuiyun.event.GroupEvent;
import com.ityun.zhihuiyun.event.HomeEvent;
import com.ityun.zhihuiyun.event.ReceiveMessageEvent;
import com.ityun.zhihuiyun.event.SendTalkBackEvent;
import com.ityun.zhihuiyun.event.SendTalkEvent;
import com.ityun.zhihuiyun.event.UserOnLineEvent;
import com.ityun.zhihuiyun.fragment.PhoneBookFragment;
import com.ityun.zhihuiyun.home.HomeActivity;
import com.ityun.zhihuiyun.talk.GroupTalkActivity;
import com.ityun.zhihuiyun.talk.SingleTalkActivity;
import com.ityun.zhihuiyun.talk.TalkUtil;
import com.ityun.zhihuiyun.util.FileUtil;
import com.ityun.zhihuiyun.util.SpUtil;
import com.ityun.zhihuiyun.util.screen.AppFileUtil;
import com.vomont.fileloadsdk.WMFileLoadSdk;
import com.wm.Constants;
import com.wm.WMIMSdk;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class IMService extends Service {

    private PowerManager.WakeLock wakeLock;

    private Context context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ImServiceBinder();
    }

    public class ImServiceBinder extends Binder {
        /**
         * 获取当前Service的实例
         *
         * @return
         */
        public IMService getService() {
            return IMService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, IMService.class.getName());
//        wakeLock.acquire();
//        Intent intent = new Intent(this, OtherService.class);
//        PendingIntent pi = PendingIntent.getService(this, 1, intent, 0);
//        AlarmManager alarm = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
//        if(alarm != null)
//        {
//            alarm.cancel(pi);
//            // 闹钟在系统睡眠状态下会唤醒系统并执行提示功能
//            alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, 2000, pi);// 确切的时间闹钟//alarm.setExact(…);
////            alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
//        }

        context = this;
    }

    @Override
    public void onDestroy() {

//        if (wakeLock != null) {
//            wakeLock.release();
//            wakeLock = null;
//        }
        super.onDestroy();
    }


}
