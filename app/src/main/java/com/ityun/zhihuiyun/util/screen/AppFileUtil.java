package com.ityun.zhihuiyun.util.screen;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import com.ityun.zhihuiyun.bean.User;

import java.io.File;

/**
 * Created by Administrator on 2018/5/30 0030.
 */

public class AppFileUtil {

    private String basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/zhihuiyun";

    private String voicePath = basePath;

    private static AppFileUtil util;

    private User user;

    public static AppFileUtil getInstance() {
        if (util == null)
            util = new AppFileUtil();
        return util;
    }

    public void init(User user) {
        this.user = user;
        voicePath = voicePath + "/" + user.getUserName() + "/voice";
        intFile();
    }


    private void intFile() {
        File voiceFile = new File(voicePath);
        if (!voiceFile.exists()) {
            voiceFile.mkdirs();
        }
    }


    public String getVoicePath() {
        return voicePath;
    }

    public void setVoicePath(String voicePath) {
        this.voicePath = voicePath;
    }


    public boolean permissionAudio(Context context) {
        //检测是否有录音权限
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            return true;
            //            Log.i(TAG, "默认无录音权限");
//            if (Build.VERSION.SDK_INT >= 23) {
//                Log.i(TAG, "系统版本不低于android6.0 ，需要动态申请权限");
//                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1001);
//            }
        } else {
            return false;
//            Log.i(TAG, "默认有录音权限");
        }
    }
}
