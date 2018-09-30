package com.ityun.zhihuiyun.event;

import com.ityun.zhihuiyun.bean.NewAccountInfo;

import java.util.List;

/**
 * Created by Administrator on 2018/7/23 0023.
 */

public class AddOrDeleteEvent {

    public List<NewAccountInfo> mlist;

    public int type;

    public int isSelect;

    public AddOrDeleteEvent(int type, List<NewAccountInfo> mlist, int isSelect) {
        this.type = type;
        this.mlist = mlist;
    }
}
