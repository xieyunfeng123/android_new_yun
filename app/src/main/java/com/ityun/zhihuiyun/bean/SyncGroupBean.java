package com.ityun.zhihuiyun.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/5/27 0027.
 */

public class SyncGroupBean implements Serializable {

    private  int result;

    private List<GroupInfo> syncgroup;

    private List<AccountIM> syncuser;

    public List<AccountIM> getSyncuser() {
        return syncuser;
    }

    public void setSyncuser(List<AccountIM> syncuser) {
        this.syncuser = syncuser;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<GroupInfo> getSyncgroup() {
        return syncgroup;
    }

    public void setSyncgroup(List<GroupInfo> syncgroup) {
        this.syncgroup = syncgroup;
    }
}
