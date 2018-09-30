package com.ityun.zhihuiyun.db;

import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.bean.HomeMessage;
import com.ityun.zhihuiyun.bean.IMMessage;
import com.ityun.zhihuiyun.dao.HomeMessageDao;
import com.ityun.zhihuiyun.dao.IMMessageDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by Administrator on 2018/5/29 0029.
 */

public class HomeMessageUtil {

    private static HomeMessageUtil getInstance;

    private HomeMessageDao dao;

    private GreenDaoManager mManager;

    public static HomeMessageUtil getInstance() {
        if (getInstance == null)
            getInstance = new HomeMessageUtil();
        return getInstance;
    }

    public HomeMessageUtil() {
        mManager = GreenDaoManager.getInstance();
        mManager.init(App.context);
        dao = mManager.getDaoSession().getHomeMessageDao();

    }

    /**
     * 添加消息
     *
     * @return 返回添加成功还是失败
     */
    public boolean insertMessage(HomeMessage message) {
        HomeMessage imMessage = dao.queryBuilder().where(HomeMessageDao.Properties.UsrId.eq(message.getUsrId()), HomeMessageDao.Properties.Id.eq(message.getId())).build().unique();
        if (imMessage != null) {
            imMessage.setMessageType(message.getMessageType());
            imMessage.setContent(message.getContent());
            imMessage.setTime(message.getTime());
            dao.insertOrReplace(imMessage);
            return false;
        }
        boolean flag;
        flag = dao.insert(message) == -1 ? false : true;
        return flag;
    }

    public void deleteMessage(HomeMessage homeMessage) {
        if (homeMessage != null)
            dao.delete(homeMessage);
    }

//

    /**
     * 根据用户id查询消息
     *
     * @param userId 用户id
     * @return
     */
    public List<HomeMessage> selectMessage(int userId, String userName) {
        QueryBuilder builder = dao.queryBuilder();
        return builder.where(HomeMessageDao.Properties.UsrId.eq(userId), HomeMessageDao.Properties.UserName.eq(userName)).list();
    }

    public HomeMessage selectMessageById(int userId, int id) {
        HomeMessage homeMessage = dao.queryBuilder().where(HomeMessageDao.Properties.UsrId.eq(userId), HomeMessageDao.Properties.Id.eq(id)).build().unique();
        return homeMessage;
    }
}
