package com.ityun.zhihuiyun.db;

import com.ityun.zhihuiyun.base.App;
import com.ityun.zhihuiyun.bean.RemarkName;
import com.ityun.zhihuiyun.dao.RemarkNameDao;

/**
 * Created by Administrator on 2018/6/21 0021.
 */

public class RemarkUtil {

    private static RemarkUtil getInstance;

    private RemarkNameDao dao;

    private GreenDaoManager mManager;

    public static RemarkUtil getInstance() {
        if (getInstance == null)
            getInstance = new RemarkUtil();
        return getInstance;
    }

    public RemarkUtil() {
        mManager = GreenDaoManager.getInstance();
        mManager.init(App.context);
        dao = mManager.getDaoSession().getRemarkNameDao();
    }

    /**
     * 添加备注名称
     *
     * @param userid
     * @param remarkAccountId
     * @param remarkName
     */
    public void addRemark(int userid, int remarkAccountId, String remarkName) {
        RemarkName remark = dao.queryBuilder().where(RemarkNameDao.Properties.UserId.eq(userid), RemarkNameDao.Properties.RemarkAccountId.eq(remarkAccountId)).build().unique();
        if (remark != null) {
            remark.setRemarkName(remarkName);
            dao.update(remark);
        } else {
            remark = new RemarkName();
            remark.setUserId(userid);
            remark.setRemarkAccountId(remarkAccountId);
            remark.setRemarkName(remarkName);
            dao.insert(remark);
        }
    }

    /**
     * 查找备注名称
     *
     * @param userid
     * @param remarkAccountId
     * @return
     */
    public RemarkName selectRemark(int userid, int remarkAccountId) {
        RemarkName remark = dao.queryBuilder().where(RemarkNameDao.Properties.UserId.eq(userid), RemarkNameDao.Properties.RemarkAccountId.eq(remarkAccountId)).build().unique();
        return remark;
    }

}
