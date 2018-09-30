package com.ityun.zhihuiyun.talk;

import java.util.List;

/**
 * Created by Administrator on 2018/6/8 0008.
 */

public class MemberBean {

    private  int chattype;

    private List<UserMember> users;

    public List<UserMember> getUsers() {
        return users;
    }

    public void setUsers(List<UserMember> users) {
        this.users = users;
    }

    public int getChattype() {
        return chattype;
    }

    public void setChattype(int chattype) {
        this.chattype = chattype;
    }
}
