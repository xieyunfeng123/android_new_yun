package com.ityun.zhihuiyun.event;

/**
 * Created by Administrator on 2018/6/8 0008.
 */

public class TalkEvent {

    private int userid;

    //0 加入 1离开
    private int join;

    public TalkEvent(int userid, int join) {
        this.userid = userid;
        this.join = join;
    }
}
