package com.ityun.zhihuiyun.bean;

/**
 * Created by Administrator on 2018/7/12 0012.
 */

public class TeamViewMember {

    /* 状态 0 接通 1挂断 2呼叫中 */
    private int status;

    private String ImageName;

    private String TextName;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }

    public String getTextName() {
        return TextName;
    }

    public void setTextName(String textName) {
        TextName = textName;
    }
}
