package com.ityun.zhihuiyun.event;

/**
 * Created by Administrator on 2018/6/1 0001.
 */

public class SendTalkEvent {


    //0成功  100关闭页面  50退出聊天的通知
    private int result;

    //0单聊 1群聊
    private int type;
    //聊天的id
    private int chatId;

    //群组聊天加入的返回的成员
    private String memberString;

    public SendTalkEvent() {

    }

    public SendTalkEvent(int result, int type, int chatId) {
        this.result = result;
        this.type = type;
        this.chatId = chatId;
    }

    public SendTalkEvent(int result, int type, int chatId,String memberString) {
        this.result = result;
        this.type = type;
        this.chatId = chatId;
        this.memberString=memberString;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public String getMemberString() {
        return memberString;
    }

    public void setMemberString(String memberString) {
        this.memberString = memberString;
    }
}
