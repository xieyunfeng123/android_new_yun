package com.ityun.zhihuiyun.util;

import android.util.Log;

import com.google.gson.Gson;
import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.GroupInfo;
import com.ityun.zhihuiyun.bean.IMMessage;
import com.ityun.zhihuiyun.bean.SyncGroupBean;
import com.ityun.zhihuiyun.bean.SysConfBean;
import com.ityun.zhihuiyun.bean.SysConfig;
import com.vomont.wmauthenticatesdk.WmAuthenticateSDK;

import java.util.Date;
import java.util.List;

public class IMClient {

    public static IMClient imClient;

    private SysConfig config;

    private List<Account> account;

    private List<GroupInfo> groupInfoList;

    public static IMClient getInstance() {
        if (imClient == null)
            imClient = new IMClient();
        return imClient;
    }

    /**
     * 创建文本消息
     *
     * @param content 文本消息
     * @param id      发送好友或者群组的id
     * @param isGroup 是否是群组
     * @retrun 消息对象
     */
    public IMMessage createTxtSendMessage(String content, int id, boolean isGroup) {
        IMMessage message = new IMMessage();
        message.setCreateTime(new Date().getTime());
        String json = WmAuthenticateSDK.getInstance().GetSysConf();
        config = new Gson().fromJson(json, SysConfig.class);
        message.setUserId(config.getSysconf().getAccountid());
        if (isGroup) {
            message.setImType(1);
        } else {
            message.setImType(0);
        }
        message.setSendName(getNameById(id, isGroup));
        message.setImId(id);
        message.setMeessageTpye(0);
        message.setMessage(content);
        message.setSendState(0);
        return message;
    }


    /**
     * 通过id 获取名称
     *
     * @param id
     * @param isGroup
     * @return
     */
    public String getNameById(int id, boolean isGroup) {
        if (isGroup) {
            if (groupInfoList != null && groupInfoList.size() != 0) {
                for (GroupInfo group : groupInfoList) {
                    if (group.getId() == id) {
                        return group.getName();
                    }
                }
            }
        } else {
            if (account != null && account.size() != 0) {
                for (Account acc : account) {
                    if (acc.getId() == id) {
                        return acc.getName();
                    }
                }
            }
        }
        return "";
    }
}

