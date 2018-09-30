package com.ityun.zhihuiyun.event;

import com.ityun.zhihuiyun.bean.IMMessage;

/**
 * Created by Administrator on 2018/5/29 0029.
 */

public class ReceiveMessageEvent {

    public IMMessage message;

    public ReceiveMessageEvent(IMMessage message) {
        this.message = message;
    }

}
