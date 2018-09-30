package com.ityun.zhihuiyun.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Administrator on 2018/5/27 0027.
 */

public class GroupInfo implements Serializable {

    private int createtime;

    private int id;

    private List<Member> member;

    private String name;

    private int owner;

    public int getCreatetime() {
        return createtime;
    }

    public void setCreatetime(int createtime) {
        this.createtime = createtime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Member> getMember() {
        return member;
    }

    public void setMember(List<Member> member) {
        this.member = member;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }


    public String getDecodeNmae() {
        if (TextUtils.isEmpty(name)) {
            return "";
        }
        String receiveText = "";
        try {
            receiveText = java.net.URLDecoder.decode(name, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return receiveText;
    }
}
