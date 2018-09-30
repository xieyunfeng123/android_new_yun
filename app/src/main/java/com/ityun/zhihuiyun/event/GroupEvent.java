package com.ityun.zhihuiyun.event;

/**
 * Created by Administrator on 2018/5/24 0024.
 */

public class GroupEvent {

    private int chatid;
    private int response;
    private int userid;

    public void setChatid(int chatid) {
        this.chatid = chatid;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getChatid() {
        return chatid;
    }

    public int getResponse() {
        return response;
    }

    public int getUserid() {
        return userid;
    }
}
