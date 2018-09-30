package com.ityun.zhihuiyun.event;

import com.ityun.zhihuiyun.bean.IntercomBean;

public class FreeRobEvent {

    public IntercomBean bean;

    public FreeRobEvent(IntercomBean bean) {
        this.bean = bean;
    }
    public IntercomBean getBean() {
        return bean;
    }
    public void setBean(IntercomBean bean) {
        this.bean = bean;
    }
}
