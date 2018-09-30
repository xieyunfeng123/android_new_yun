package com.ityun.zhihuiyun.db;

import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.bean.IMMessage;
import com.ityun.zhihuiyun.dao.IMMessageDao;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.List;

/**
 * Created by Administrator on 2018/5/29 0029.
 */

public class IMUtil {

    private static IMUtil getInstance;

    private IMMessageDao dao;

    private GreenDaoManager mManager;

    public static IMUtil getInstance() {
        if (getInstance == null)
            getInstance = new IMUtil();
        return getInstance;
    }

    public IMUtil() {
        mManager = GreenDaoManager.getInstance();
        mManager.init(App.context);
        dao = mManager.getDaoSession().getIMMessageDao();
    }

    /**
     * 添加消息
     *
     * @return 返回添加成功还是失败
     */
    public boolean insertMessage(IMMessage message) {
        IMMessage imMessage = dao.queryBuilder().where(IMMessageDao.Properties.UserId.eq(message.getUserId()), IMMessageDao.Properties.CreateTime.eq(message.getCreateTime()), IMMessageDao.Properties.FileUrl.eq(message.getFileUrl() != null ? message.getFileUrl() : "")).build().unique();
        if (imMessage != null) {
            return false;
        }
        boolean flag;
        flag = mManager.getDaoSession().getIMMessageDao().insert(message) == -1 ? false : true;
        return flag;
    }


    /**
     * 根据类型和群id或者用户id查询消息
     *
     * @param userId 用户id
     * @param imId
     * @return
     */
    public List<IMMessage> selectMeaage(int userId, int imId) {
        QueryBuilder builder = dao.queryBuilder();
        return builder.where(IMMessageDao.Properties.UserId.eq(userId), IMMessageDao.Properties.ImId.eq(imId)).list();
    }


    /**
     * 根据类型和群id或者用户id查询消息
     *
     * @param userId 用户id
     * @param imId
     * @return
     */
    public List<IMMessage> selectMeaageByRead(int userId, int imId, int read) {
        QueryBuilder builder = dao.queryBuilder();
        return builder.where(IMMessageDao.Properties.IsSelf.eq(1), IMMessageDao.Properties.UserId.eq(userId), IMMessageDao.Properties.ImId.eq(imId), IMMessageDao.Properties.IsRead.eq(read)).list();
    }

    /**
     * 根据类型查询消息
     *
     * @param userId 用户id
     * @return
     */
    public List<IMMessage> selectAllMeaageByRead(int userId, int read) {
        QueryBuilder builder = dao.queryBuilder();
        return builder.where(IMMessageDao.Properties.IsSelf.eq(1), IMMessageDao.Properties.UserId.eq(userId), IMMessageDao.Properties.IsRead.eq(read)).list();
    }

    /**
     * 根据id 更新进度
     */
    public void upDateMessageByLoadId(IMMessage imMessage) {
        if (imMessage != null) {
            dao.update(imMessage);
        }
    }

    /**
     * 查找id
     *
     * @param useid
     * @param loadId
     * @return
     */
    public IMMessage selectMessageByLoadId(int useid, int loadId) {
        IMMessage imMessage = dao.queryBuilder().where(IMMessageDao.Properties.UserId.eq(useid), IMMessageDao.Properties.LoadId.eq(loadId)).build().unique();
        return imMessage;
    }


    /**
     * 修改是否已读
     *
     * @param list 聊天信息列表
     */
    public void saveNoteLists(final List<IMMessage> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        dao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    IMMessage message = list.get(i);
                    dao.insertOrReplace(message);
                }
            }
        });
    }

    /**
     * 修改上传状态
     *
     * @param userId 用户id
     * @param time   发送时间
     * @param state  状态 0 成功 1正在发送 2失败
     */
    public void upDataSendState(int userId, long time, int state) {
        IMMessage message = dao.queryBuilder().where(IMMessageDao.Properties.UserId.eq(userId), IMMessageDao.Properties.CreateTime.eq(time)).build().unique();
        if (message != null) {
            message.setSendState(state);
            dao.update(message);
        }
    }

    /**
     * 修改上传状态
     */
    public void upDataIM(IMMessage message) {
        if (message != null) {
            dao.update(message);
        }
    }

    /**
     * 声音已经打开过
     *
     * @param message
     */
    public void upDataVoiceOpen(IMMessage message) {
        if (message != null)
            dao.update(message);
    }


    public void deleteDB(int userId, int imId) {
        QueryBuilder builder = dao.queryBuilder();
        List<IMMessage> messageList = builder.where(IMMessageDao.Properties.UserId.eq(userId), IMMessageDao.Properties.ImId.eq(imId)).list();
        dao.deleteInTx(messageList);
    }


    public void deleteMessage(IMMessage message) {
        dao.delete(message);
    }


}
