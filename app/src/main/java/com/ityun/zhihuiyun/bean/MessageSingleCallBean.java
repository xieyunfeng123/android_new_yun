package com.ityun.zhihuiyun.bean;

/**
 * Created by Administrator on 2018/5/29 0029.
 */

public class MessageSingleCallBean {

    private String content;
    private int sendtime;
    private int srcuserId;
    private int type;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSendtime() {
        return sendtime;
    }

    public void setSendtime(int sendtime) {
        this.sendtime = sendtime;
    }

    public int getSrcuserId() {
        return srcuserId;
    }

    public void setSrcuserId(int srcuserId) {
        this.srcuserId = srcuserId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
