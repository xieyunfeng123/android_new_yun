package com.ityun.zhihuiyun.bean;

import android.text.TextUtils;

import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.db.RemarkUtil;
import com.zhy.clientsdk.WMAccountInfo;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/25 0025.
 */

public class NewAccountInfo implements Serializable {
    private int accountId;
    private String accountName;
    private String macCode;
    private String remarkName;
    private int status;
    private String sort;
    private boolean isChoose;

    public NewAccountInfo(Account accountInfo) {
        if (accountInfo != null) {
            accountId = accountInfo.getId();
            accountName = accountInfo.getName();
//            macCode = accountInfo.get;
//            remarkName = accountInfo.getRemarkName();
            status = accountInfo.getStatus();
        }
    }
    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getAccountId() {
        return this.accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return this.accountName;
    }
    public  String  getAccountRemarkName()
    {
        SysConfBean bean = App.getInstance().getSysConfBean();
        if (bean != null) {
            RemarkName remarkName = RemarkUtil.getInstance().selectRemark(bean.getAccountid(), accountId);
            if (remarkName != null && !TextUtils.isEmpty(remarkName.getRemarkName().trim())) {
                return remarkName.getRemarkName();
            }
        }
        return "";
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getMacCode() {
        return this.macCode;
    }

    public void setMacCode(String macCode) {
        this.macCode = macCode;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    public String getRemarkName() {
        return this.remarkName;
    }
}
