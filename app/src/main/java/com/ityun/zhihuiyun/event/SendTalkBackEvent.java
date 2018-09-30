package com.ityun.zhihuiyun.event;

/**
 * Created by Administrator on 2018/6/1 0001.
 */

public class SendTalkBackEvent {

    private int chatid;
    private int response;
    private int userid;


    public SendTalkBackEvent() {

    }

    public SendTalkBackEvent(int chatid,int response,int userid) {
        this.chatid=chatid;
        this.response=response;
        this.userid=userid;
    }

    public int getChatid() {
        return chatid;
    }

    public void setChatid(int chatid) {
        this.chatid = chatid;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
