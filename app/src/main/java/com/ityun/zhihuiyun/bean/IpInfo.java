package com.ityun.zhihuiyun.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/21 0021.
 */

public class IpInfo implements Serializable {


    private String ip;


    private int port;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
