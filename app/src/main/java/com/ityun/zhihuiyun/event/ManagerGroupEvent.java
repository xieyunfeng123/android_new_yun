package com.ityun.zhihuiyun.event;

import com.ityun.zhihuiyun.bean.Account;

import java.util.ArrayList;
import java.util.List;

public class ManagerGroupEvent {
    private List<Account> accounts = new ArrayList<>();

    public ManagerGroupEvent(List<Account> accounts) {
        this.accounts = accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}
