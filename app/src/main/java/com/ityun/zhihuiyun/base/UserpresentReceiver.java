package com.ityun.zhihuiyun.base;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

/**
 * Created by Administrator on 2018/7/11 0011.
 */

public class UserpresentReceiver extends BroadcastReceiver {

    private String TAG = "UserpresentReceiver";

//    private Vibrator vibrator;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");
        String action = intent.getAction();
//        vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            Log.e(TAG, "屏幕亮了啊");
            context.stopService(new Intent(context.getApplicationContext(), ProtectService.class));
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            Log.e(TAG, "屏幕暗了啊");
           /* vibrator.vibrate(new long[]{1000, 1500, 500, 1500, 500,
                    1500, 500,
            }, -1);*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context.getApplicationContext(), ProtectService.class));

            } else {
                context.startService(new Intent(context.getApplicationContext(), ProtectService.class));
            }
//            context.startService(new Intent(context.getApplicationContext(), ProtectService.class));
        } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
            Log.e(TAG, "解锁了哦");
        } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
            Log.e(TAG, " receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS");
        }
    }

}
