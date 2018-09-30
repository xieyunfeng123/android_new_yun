package com.ityun.zhihuiyun.event;

/**
 * Created by Administrator on 2018/5/29 0029.
 */

public class HttpEvent {

    public int result;

    public String call;
    public HttpEvent(int result, String call) {
        this.result = result;
        this.call = call;
    }
}
