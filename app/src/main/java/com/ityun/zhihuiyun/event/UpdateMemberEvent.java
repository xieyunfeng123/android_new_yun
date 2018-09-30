package com.ityun.zhihuiyun.event;

public class UpdateMemberEvent {
    public int id;
    public int online;
    public UpdateMemberEvent(int id, int online) {
        this.id = id;
        this.online = online;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setOnline(int online) {
        this.online = online;
    }
    public int getId() {
        return id;
    }
    public int getOnline() {
        return online;
    }
}
