package com.ityun.zhihuiyun.event;

import com.ityun.zhihuiyun.bean.NewAccountInfo;
import java.util.List;

/**
 * Created by Administrator on 2018/5/27 0027.
 */

public class SelectUserEvent {

    public List<NewAccountInfo> mlist;

    public SelectUserEvent(List<NewAccountInfo> mlist) {
        this.mlist = mlist;
    }
}
