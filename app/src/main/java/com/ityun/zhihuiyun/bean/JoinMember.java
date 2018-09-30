package com.ityun.zhihuiyun.bean;

import java.util.List;

public class JoinMember {


    private int chattype;

    private List<ReceiveMember> users;

    public int getChattype() {
        return chattype;
    }

    public void setChattype(int chattype) {
        this.chattype = chattype;
    }

    public List<ReceiveMember> getUsers() {
        return users;
    }

    public void setUsers(List<ReceiveMember> users) {
        this.users = users;
    }
}
