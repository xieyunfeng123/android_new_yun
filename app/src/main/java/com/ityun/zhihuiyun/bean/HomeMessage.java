package com.ityun.zhihuiyun.bean;

import android.text.TextUtils;

import com.ityun.zhihuiyun.db.RemarkUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2018/5/31 0031.
 */

@Entity
public class HomeMessage {

    @Id(autoincrement = true)
    private Long homeId;

    //用户的id
    private int usrId;

    private String userName;

    //消息的id
    private int id;
    //消息的名称
    private String name;
    //消息的类型 0个人 1群组
    private int imType;
    //消息的时间
    private long time;
    //消息的内容
    private String content;
    //消息的文本类型
    private int messageType;

    @Generated(hash = 1055836995)
    public HomeMessage(Long homeId, int usrId, String userName, int id, String name,
                       int imType, long time, String content, int messageType) {
        this.homeId = homeId;
        this.usrId = usrId;
        this.userName = userName;
        this.id = id;
        this.name = name;
        this.imType = imType;
        this.time = time;
        this.content = content;
        this.messageType = messageType;
    }

    @Generated(hash = 1526166140)
    public HomeMessage() {
    }

    public Long getHomeId() {
        return this.homeId;
    }

    public void setHomeId(Long homeId) {
        this.homeId = homeId;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        if (imType == 0) {
            RemarkName remarkName = RemarkUtil.getInstance().selectRemark(usrId, id);
            if (remarkName != null && !TextUtils.isEmpty(remarkName.getRemarkName().trim())) {
                return remarkName.getRemarkName();
            }
        }
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImType() {
        return this.imType;
    }

    public void setImType(int imType) {
        this.imType = imType;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        if (getMessageType() == 1) {
            return "[语音]";
        } else if (getMessageType() == 2) {
            return "[图片]";
        } else if (getMessageType() == 4) {
            return "[位置]";
        } else if (getMessageType() == 3) {
            return "[视频]";
        }
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMessageType() {
        return this.messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getUsrId() {
        return this.usrId;
    }

    public void setUsrId(int usrId) {
        this.usrId = usrId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
