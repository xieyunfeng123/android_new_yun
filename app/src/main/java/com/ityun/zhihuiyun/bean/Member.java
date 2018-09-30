package com.ityun.zhihuiyun.bean;

import android.text.TextUtils;

import com.ityun.zhihuiyun.base.App;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/27 0027.
 */

public class Member implements Serializable {

    private int id;

    /**
     * 0 初始化
     * 1 已加入
     * 2已挂断
     * 3 已抢麦
     */
    private int memberState;

    private String name;

    private String sort;

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getName() {
        if (TextUtils.isEmpty(name)) {
            if (App.accountList != null) {
                String accoutName = "";
                for (Account account : App.accountList) {
                    if (account.getId() == id) {
                        accoutName = account.getName();
                    }
                }
                return accoutName;
            }


            return "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMemberState() {
        return memberState;
    }

    public void setMemberState(int memberState) {
        this.memberState = memberState;
    }

    public int getId() {
        return id;
    }

    public Member() {

    }

    public Member(NewAccountInfo accountInfo) {
        if (accountInfo != null)
            id = accountInfo.getAccountId();
    }

    public Member(Account account) {
        if (account != null)
            id = account.getId();
    }


    public void setId(int id) {
        this.id = id;
    }
}
