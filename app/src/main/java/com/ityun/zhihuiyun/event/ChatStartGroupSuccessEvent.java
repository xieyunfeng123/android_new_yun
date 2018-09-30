package com.ityun.zhihuiyun.event;

public class ChatStartGroupSuccessEvent {

    private int chatId;

    public ChatStartGroupSuccessEvent(int chatId) {
        this.chatId = chatId;
    }


    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }
}
