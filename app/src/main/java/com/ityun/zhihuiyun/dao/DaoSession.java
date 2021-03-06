package com.ityun.zhihuiyun.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.ityun.zhihuiyun.bean.HomeMessage;
import com.ityun.zhihuiyun.bean.IMMessage;
import com.ityun.zhihuiyun.bean.RemarkName;

import com.ityun.zhihuiyun.dao.HomeMessageDao;
import com.ityun.zhihuiyun.dao.IMMessageDao;
import com.ityun.zhihuiyun.dao.RemarkNameDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig homeMessageDaoConfig;
    private final DaoConfig iMMessageDaoConfig;
    private final DaoConfig remarkNameDaoConfig;

    private final HomeMessageDao homeMessageDao;
    private final IMMessageDao iMMessageDao;
    private final RemarkNameDao remarkNameDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        homeMessageDaoConfig = daoConfigMap.get(HomeMessageDao.class).clone();
        homeMessageDaoConfig.initIdentityScope(type);

        iMMessageDaoConfig = daoConfigMap.get(IMMessageDao.class).clone();
        iMMessageDaoConfig.initIdentityScope(type);

        remarkNameDaoConfig = daoConfigMap.get(RemarkNameDao.class).clone();
        remarkNameDaoConfig.initIdentityScope(type);

        homeMessageDao = new HomeMessageDao(homeMessageDaoConfig, this);
        iMMessageDao = new IMMessageDao(iMMessageDaoConfig, this);
        remarkNameDao = new RemarkNameDao(remarkNameDaoConfig, this);

        registerDao(HomeMessage.class, homeMessageDao);
        registerDao(IMMessage.class, iMMessageDao);
        registerDao(RemarkName.class, remarkNameDao);
    }
    
    public void clear() {
        homeMessageDaoConfig.clearIdentityScope();
        iMMessageDaoConfig.clearIdentityScope();
        remarkNameDaoConfig.clearIdentityScope();
    }

    public HomeMessageDao getHomeMessageDao() {
        return homeMessageDao;
    }

    public IMMessageDao getIMMessageDao() {
        return iMMessageDao;
    }

    public RemarkNameDao getRemarkNameDao() {
        return remarkNameDao;
    }

}
