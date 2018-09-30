package com.ityun.zhihuiyun.bean;

import android.text.TextUtils;
import com.ityun.zhihuiyun.db.RemarkUtil;
import android.util.Log;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/29 0029.
 */

@Entity
public class IMMessage implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    private Long im_id;

    /**
     * 用户的id
     */
    private int userId;

    /**
     * 0 单人聊天
     * 1 群组聊天
     */
    private int imType;

    /**
     * 发消息的人或群组的id
     */
    private int imId;

    /**
     * 如果是群组 发消息的人的id
     */
    private int messageSendId;

    /**
     * 如果是群组发送者的名称
     */
    private String memberName;

    /**
     * 发送的人名
     */
    private String sendName;

    /**
     * 是否是自己
     * 0 自己 1别人
     */
    private int isSelf;

    /**
     * 0-文本，1-语言，2-图片，3-视频，4-位置
     */
    private int meessageTpye;
    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 语音的路径
     */
    private String voicePath;

    /**
     * 录音是否打开过  0没有 1打开过
     */
    private int voiceHasOpen;

    /**
     * 图片的路径
     */
    private String imagePath;

    /**
     * 上传的id
     */
    private int loadId;

    /**
     * 文件上传进度
     */
    private int fileProgress;

    /**
     * 文件上传后的路径
     */
    private String fileUrl;

    /**
     * 文本
     */
    private String message;

    /**
     * 是否已读
     * 0未读 1已读
     */
    private int isRead;

    /**
     * 0 成功 1正在发送  2 发送失败
     *
     */
    private int sendState;


    @Generated(hash = 2029415031)
    public IMMessage(Long im_id, int userId, int imType, int imId,
                     int messageSendId, String memberName, String sendName, int isSelf,
                     int meessageTpye, long createTime, String voicePath, int voiceHasOpen,
                     String imagePath, int loadId, int fileProgress, String fileUrl,
                     String message, int isRead, int sendState) {
        this.im_id = im_id;
        this.userId = userId;
        this.imType = imType;
        this.imId = imId;
        this.messageSendId = messageSendId;
        this.memberName = memberName;
        this.sendName = sendName;
        this.isSelf = isSelf;
        this.meessageTpye = meessageTpye;
        this.createTime = createTime;
        this.voicePath = voicePath;
        this.voiceHasOpen = voiceHasOpen;
        this.imagePath = imagePath;
        this.loadId = loadId;
        this.fileProgress = fileProgress;
        this.fileUrl = fileUrl;
        this.message = message;
        this.isRead = isRead;
        this.sendState = sendState;
    }

    @Generated(hash = 1610895367)
    public IMMessage() {

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSendName() {

        if (imType == 0) {
            RemarkName remarkName = RemarkUtil.getInstance().selectRemark(userId, imId);
            if (remarkName != null && !TextUtils.isEmpty(remarkName.getRemarkName())) {
                return remarkName.getRemarkName();
            }
        }

        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        Log.e("insert", "-------sendContent------");
        this.message = message;
    }


    public int getImType() {
        return imType;
    }

    public void setImType(int imType) {
        this.imType = imType;
    }

    public int getImId() {
        return imId;
    }

    public void setImId(int imId) {
        this.imId = imId;
    }

    public int getMessageSendId() {
        return messageSendId;
    }

    public void setMessageSendId(int messageSendId) {
        this.messageSendId = messageSendId;
    }

    public int getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(int isSelf) {
        this.isSelf = isSelf;
    }

    public int getMeessageTpye() {
        return meessageTpye;
    }

    public void setMeessageTpye(int meessageTpye) {
        this.meessageTpye = meessageTpye;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public void setVoicePath(String voicePath) {
        this.voicePath = voicePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public int getSendState() {
        return sendState;
    }

    public void setSendState(int sendState) {
        this.sendState = sendState;
    }

    public String getMemberName() {
        RemarkName remarkName = RemarkUtil.getInstance().selectRemark(userId, messageSendId);
        if (remarkName != null && !TextUtils.isEmpty(remarkName.getRemarkName())) {
            return remarkName.getRemarkName();
        }
        return this.memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }


    public Long getIm_id() {
        return this.im_id;
    }

    public void setIm_id(Long im_id) {
        this.im_id = im_id;
    }

    public int getVoiceHasOpen() {
        return this.voiceHasOpen;
    }

    public void setVoiceHasOpen(int voiceHasOpen) {
        this.voiceHasOpen = voiceHasOpen;
    }

    public int getFileProgress() {
        return this.fileProgress;
    }

    public void setFileProgress(int fileProgress) {
        this.fileProgress = fileProgress;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getLoadId() {
        return this.loadId;
    }

    public void setLoadId(int loadId) {
        this.loadId = loadId;
    }

}
