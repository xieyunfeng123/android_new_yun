package com.ityun.zhihuiyun.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Administrator on 2018/6/21 0021.
 */

@Entity
public class RemarkName {

    @Id(autoincrement =true)
    private  Long id;

    private int userId;

    private int remarkAccountId;

    private String  remarkName;

    @Generated(hash = 1409674725)
    public RemarkName(Long id, int userId, int remarkAccountId, String remarkName) {
        this.id = id;
        this.userId = userId;
        this.remarkAccountId = remarkAccountId;
        this.remarkName = remarkName;
    }

    @Generated(hash = 1872251461)
    public RemarkName() {
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRemarkAccountId() {
        return this.remarkAccountId;
    }

    public void setRemarkAccountId(int remarkAccountId) {
        this.remarkAccountId = remarkAccountId;
    }

    public String getRemarkName() {
        return this.remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
