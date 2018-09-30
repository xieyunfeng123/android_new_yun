package com.ityun.zhihuiyun.event;

public class ChatJoinGroupEvent {

    private int chatId;

    private String memberString;

    public ChatJoinGroupEvent(int chatId, String memberString) {
        this.chatId = chatId;
        this.memberString = memberString;
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
