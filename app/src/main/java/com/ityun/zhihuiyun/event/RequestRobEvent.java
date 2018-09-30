package com.ityun.zhihuiyun.event;

import com.ityun.zhihuiyun.bean.IntercomBean;

public class RequestRobEvent {

    public IntercomBean bean;

    public RequestRobEvent(IntercomBean bean) {
        this.bean = bean;
    }
    public IntercomBean getBean() {
        return bean;
    }
    public void setBean(IntercomBean bean) {
        this.bean = bean;
    }
}
