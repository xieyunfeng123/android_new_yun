package com.ityun.zhihuiyun.event;

/**
 * Created by Administrator on 2018/5/27 0027.
 */

public class FinishEvent {

    public String activity;

    public int id;

    public FinishEvent(String activity) {
        this.activity = activity;
        id=-10;
    }

    public FinishEvent(String activity,int id) {
        this.activity = activity;
        this.id=id;
    }
}
