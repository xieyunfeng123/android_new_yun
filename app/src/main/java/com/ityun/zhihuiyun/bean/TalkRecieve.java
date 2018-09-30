package com.ityun.zhihuiyun.bean;

import com.wm.Constants;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/1 0001.
 */

public class TalkRecieve implements Serializable {

    private int chatid;

    private int chattype;

    private int sponsorid;

    private boolean isJoin;

    public boolean isJoin() {
        return isJoin;
    }

    public void setJoin(boolean join) {
        isJoin = join;
    }

    public int getChatid() {
        return chatid;
    }

    public void setChatid(int chatid) {
        this.chatid = chatid;
    }

    public int getChattype() {
        return chattype;
    }

    public void setChattype(int chattype) {
        this.chattype = chattype;
    }

    public int getSponsorid() {
        return sponsorid;
    }

    public void setSponsorid(int sponsorid) {
        this.sponsorid = sponsorid;
    }


    public int getCallType() {
        return Constants.WM_GetCallType(chattype);
    }

    public int getGroupId() {
        return Constants.WM_GetGroupId(chattype);
    }
}
