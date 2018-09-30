package com.ityun.zhihuiyun.bean;

import java.io.Serializable;
import java.util.List;

public class CallStatus implements Serializable {
    private int callid;
    private int calltype;
    private int groupid;
    private List<UserStatus> usercallstatus;

    public int getCallid() {
        return callid;
    }

    public void setCallid(int callid) {
        this.callid = callid;
    }

    public int getCalltype() {
        return calltype;
    }

    public void setCalltype(int calltype) {
        this.calltype = calltype;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public List<UserStatus> getUsercallstatus() {
        return usercallstatus;
    }

    public void setUsercallstatus(List<UserStatus> usercallstatus) {
        this.usercallstatus = usercallstatus;
    }

}
