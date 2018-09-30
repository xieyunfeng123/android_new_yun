package com.ityun.zhihuiyun.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/6/11 0011.
 */

public class RequstGroup {

    private int  sponsorid;

    private GroupInfo groupinfo;


    private int  groupid;


    private List<Member> joinids;

    private int  kickoutid;


    private int userid;

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public List<Member> getJoinids() {
        return joinids;
    }

    public void setJoinids(List<Member> joinids) {
        this.joinids = joinids;
    }

    public int getKickoutid() {
        return kickoutid;
    }

    public void setKickoutid(int kickoutid) {
        this.kickoutid = kickoutid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getSponsorid() {
        return sponsorid;
    }

    public void setSponsorid(int sponsorid) {
        this.sponsorid = sponsorid;
    }

    public GroupInfo getGroupinfo() {
        return groupinfo;
    }

    public void setGroupinfo(GroupInfo groupinfo) {
        this.groupinfo = groupinfo;
    }




}
