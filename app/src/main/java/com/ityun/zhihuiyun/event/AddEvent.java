package com.ityun.zhihuiyun.event;

import com.ityun.zhihuiyun.bean.Account;
import com.ityun.zhihuiyun.bean.NewAccountInfo;

import java.util.List;

/**
 * Created by Administrator on 2018/7/23 0023.
 */

public class AddEvent {

    public List<Account> mlist;

    public int type;

    public AddEvent(int type, List<Account> mlist) {
        this.type = type;
        this.mlist = mlist;
    }
}
