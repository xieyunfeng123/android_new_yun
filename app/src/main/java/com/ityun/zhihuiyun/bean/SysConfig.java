package com.ityun.zhihuiyun.bean;

/**
 * Created by Administrator on 2018/5/30 0030.
 */

public class SysConfig {

    private int userid;

    private SysConfBean sysconf;

    private String  signature;


    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public SysConfBean getSysconf() {
        return sysconf;
    }

    public void setSysconf(SysConfBean sysconf) {
        this.sysconf = sysconf;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
