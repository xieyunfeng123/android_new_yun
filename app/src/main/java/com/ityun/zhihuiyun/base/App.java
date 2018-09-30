package com.ityun.zhihuiyun.base;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.Department;
import com.ityun.zhihuiyun.bean.IpInfo;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.bean.SysConfig;
import com.ityun.zhihuiyun.util.SpUtil;
import com.vomont.wmauthenticatesdk.CWMAuthEnvConf;
import com.vomont.wmauthenticatesdk.WMEventCallBack;
import com.vomont.wmauthenticatesdk.WmAuthenticateSDK;
import com.zhy.clientsdk.ZHYClientSdk;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2018/5/21 0021.
 */

public class App extends Application {

    private static App clientApp = new App();

    private int logLevel = 63;

    public static SysConfBean sysConfBean;

    public static Context context;

    public static List<Account> accountList;

    private  Department department;

    public static App getInstance() {
        return clientApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        CrashHandler.getInstance().init(this);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);

        IpInfo ipInfo = SpUtil.getIp();
        if (ipInfo != null && !TextUtils.isEmpty(ipInfo.getIp())) {
            old_ip = ipInfo.getIp();
            if (ipInfo.getPort() != 0) {
                old_oprt = ipInfo.getPort();
            } else {
                old_oprt = 8001;
            }
        }
        initSDK();
    }


    public void initSDK() {
        //聊天app的初始化
        ZHYClientSdk.getInstance().init(logLevel);
        //认证SDK的状态的监听
        WmAuthenticateSDK.getInstance().setWMEventCallBack((i, i1) -> {
            if (i1 == 0) {
                switch (i) {
                    case ConstantAuthenticate.WM_Authenticate_SvrMsgId_ReConnect:
                        //重连通知
                        getSysConf();
                        break;
                    case ConstantAuthenticate.WM_Authenticate_SvrMsgId_UpdateSignature:
                        getSysConf();
                        break;
                    case ConstantAuthenticate.WM_Authenticate_SvrMsgId_DisConnect:
                        //断线通知
                        break;
                    case ConstantAuthenticate.WM_Authenticate_SvrMsgId_KickOut:
                        //被服务器踢通知
                        break;
                    default:
                        break;
                }

            }
        });

    }

    /**
     * {
     * "accountid" : 1,
     * "imsvrhttpport" : 9050,
     * "imsvrid" : 1,
     * "imsvrip" : "192.168.0.88",
     * "imsvrport" : 9000,
     * "locationsvrip" : "192.168.0.199",
     * "locationsvrport" : 8051,
     * "locationuploadinterval" : 300,
     * "signature" : "c4ca4238a0b92382",
     * "thirdparty" : [
     * {
     * "swsignature" : "abscdwecsdvfbguyjuyhmnghnfr"
     * }
     * ],
     * "userid" : 1,
     * "version" : [
     * {
     * "number" : "1.0.0.1",
     * "url" : "http://192.168.0.88/123.apk"
     * }
     * ],
     * "veyedatasvrip" :
     */
    private void getSysConf() {
        String json = WmAuthenticateSDK.getInstance().GetSysConf();
        Log.e("maccode",json);
        if (json != null) {
            try {
                SysConfig scb = new Gson().fromJson(json, SysConfig.class);
                if (scb != null && scb.getSysconf() != null)
                    //把这个对象存起来
                    setSysConfBean(scb.getSysconf());
            } catch (Exception e) {
            }
        }
    }


    //保存签名信息
    public void setSysConfBean(SysConfBean scb) {
        sysConfBean = scb;
    }

    //获取签名信息
    public SysConfBean getSysConfBean() {
        return sysConfBean;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        App.accountList = accountList;
    }

    /**
     * 登录的接口
     *
     * @param name
     * @param password
     * @return 0表示成功 其他的是失败原因
     */
    private String mImei;
    //114.115.140.125
    //192.168.0.187
    public static String old_ip = "115.238.61.114";
    public static int old_oprt = 8001;

    public int login(String name, String password) {
        //认证SDK的初始化
        CWMAuthEnvConf con = new CWMAuthEnvConf();
        con.setLogLevel(logLevel);
        con.setServerIP(old_ip);
        con.setServerPort(old_oprt);
        WmAuthenticateSDK.getInstance().uninit();
        WmAuthenticateSDK.getInstance().init(con);
        int result = WmAuthenticateSDK.getInstance().login(name, password, 1);
        if (result == 0) {
            getSysConf();
        }
        return result;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
