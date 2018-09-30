package com.ityun.zhihuiyun.event;

public class ChatStartGroupFailEvent {

    private int chatId;

    public ChatStartGroupFailEvent(int chatId) {
        this.chatId = chatId;
    }


    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }
}
