package com.ityun.zhihuiyun.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.bean.Clarity;
import com.ityun.zhihuiyun.bean.IpInfo;
import com.ityun.zhihuiyun.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/21 0021.
 */

public class SpUtil {


    private static String IP_NAME = "ip";

    private static String USER = "user";

    private static String CLARITY = "clarity";

    private static String ISOPENVOICE = "isopenvoice";

    private static String ISSHOCK = "isshock";


    public static void setIp(IpInfo info) {
        SharedPreferences sf = App.context.getSharedPreferences(IP_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String ip = gson.toJson(info, IpInfo.class);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("ip", ip);
        editor.commit();
    }


    public static IpInfo getIp() {
        SharedPreferences sf = App.context.getSharedPreferences(IP_NAME, Context.MODE_PRIVATE);
        String ip = sf.getString("ip", "");
        Gson gson = new Gson();
        IpInfo ipInfo = gson.fromJson(ip, IpInfo.class);
        return ipInfo;
    }


    public static void setListIp(List<IpInfo> mlist) {
        SharedPreferences sf = App.context.getSharedPreferences(IP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mlist);
        editor.putString("ips", json);
        editor.commit();
    }


    public static List<IpInfo> getListIp() {
        List<IpInfo> mlist = new ArrayList<>();
        SharedPreferences sf = App.context.getSharedPreferences(IP_NAME, Context.MODE_PRIVATE);
        String json = sf.getString("ips", "");
        if (!TextUtils.isEmpty(json)) {
            Gson gson = new Gson();
            mlist = gson.fromJson(json, new TypeToken<List<IpInfo>>() {
            }.getType());
            return mlist;
        }
        return mlist;
    }


    public static void setLoginIp(String ip, int port) {
        SharedPreferences sf = App.context.getSharedPreferences(IP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("logionIp", ip);
        editor.putInt("loginPort", port);
        editor.commit();
    }

    public static IpInfo getLoginIp() {
        SharedPreferences sf = App.context.getSharedPreferences(IP_NAME, Context.MODE_PRIVATE);
        String ip = sf.getString("loginIp", "");
        int port = sf.getInt("loginPort", 0);
        IpInfo ipInfo = new IpInfo();
        ipInfo.setIp(ip);
        ipInfo.setPort(port);
        return ipInfo;
    }


    public static void setUser(String name, String psd, int id) {
        SharedPreferences sf = App.context.getSharedPreferences(USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("name", name);
        editor.putString("password", psd);
        editor.putInt("id", id);
        editor.commit();
    }

    /**
     * 更新id
     * @param id
     */
    public static void upDataId(int id) {
        SharedPreferences sf = App.context.getSharedPreferences(USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putInt("id", id);
        editor.commit();
    }

    /**
     * 获取登录缓存
     * @return
     */
    public static User getUser() {
        SharedPreferences sf = App.context.getSharedPreferences(USER, Context.MODE_PRIVATE);
        User user = new User();
        user.setUserName(sf.getString("name", ""));
        user.setPassword(sf.getString("password", ""));
        user.setId(sf.getInt("id", 0));
        return user;
    }

    /**
     * 保存登录数据
     * @param data
     */
    public static void setLoginData(String data) {
        SharedPreferences sf = App.context.getSharedPreferences(USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("data", data);
        editor.commit();
    }


    /**
     * 获取画面清晰度的缓存
     *
     * @param context
     * @return
     */
    public static Clarity getClarity(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CLARITY, Context.MODE_PRIVATE);
        Clarity myClarity = new Clarity();
        myClarity.setClarity(sharedPreferences.getInt("clarity", 1));
        return myClarity;
    }

    /**
     * 将画面清晰度的设置保存到本地
     *
     * @param context 上下文
     * @param clarity 缓存 0为标清 1为高清 2为全高清
     */
    public static void setClarity(Context context, int clarity) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CLARITY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("clarity", clarity);
        editor.commit();
    }

    /**
     * 声音是否打开
     *
     * @param context     上下文
     * @param isOpenVoice 0为关闭 1为打开
     */
    public static void setVoice(Context context, int isOpenVoice) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ISOPENVOICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("isOpenVoice", isOpenVoice);
        editor.commit();
    }

    /**
     * 获取声音是否打开的状态
     *
     * @param context 上下文
     * @return 0为关闭 1为打开
     */
    public static int getVoice(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ISOPENVOICE, Context.MODE_PRIVATE);
        int isOpenVoice = sharedPreferences.getInt("isOpenVoice", 0);
        return isOpenVoice;
    }


    /**
     * 振动是否打开
     *
     * @param context 上下文
     * @param isShock 0为关闭 1为打开
     */
    public static void setShock(Context context, int isShock) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ISSHOCK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("isShock", isShock);
        editor.commit();
    }


    /**
     * 获取声音是否打开的状态
     *
     * @param context 上下文
     * @return 0为关闭 1为打开
     */
    public static int getShock(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ISSHOCK, Context.MODE_PRIVATE);
        int isShock = sharedPreferences.getInt("isShock", 0);
        return isShock;
    }
}
