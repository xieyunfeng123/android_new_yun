package com.ityun.zhihuiyun.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/5/28 0028.
 */

public class AccountBean implements Serializable {
    private int result;

    private int accouncount;

    private List<Account> accounts;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getAccouncount() {
        return accouncount;
    }

    public void setAccouncount(int accouncount) {
        this.accouncount = accouncount;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
