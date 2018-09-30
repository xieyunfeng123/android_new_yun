package com.ityun.zhihuiyun.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/21 0021.
 */

public class User implements Serializable {

    private int id;

    private String  userName;

    private  String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
