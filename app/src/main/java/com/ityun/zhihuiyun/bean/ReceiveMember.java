package com.ityun.zhihuiyun.bean;

import java.io.Serializable;

public class ReceiveMember implements Serializable{

    private int userid;

    private int callstatus;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getCallstatus() {
        return callstatus;
    }

    public void setCallstatus(int callstatus) {
        this.callstatus = callstatus;
    }
}
