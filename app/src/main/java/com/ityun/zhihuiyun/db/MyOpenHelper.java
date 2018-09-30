package com.ityun.zhihuiyun.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.ityun.zhihuiyun.bean.RemarkName;
import com.ityun.zhihuiyun.dao.DaoMaster;
import com.ityun.zhihuiyun.dao.HomeMessageDao;
import com.ityun.zhihuiyun.dao.IMMessageDao;
import com.ityun.zhihuiyun.dao.RemarkNameDao;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Administrator on 2018/6/20 0020.
 */

public class MyOpenHelper extends DaoMaster.OpenHelper {
    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {

        //把需要管理的数据库表DAO作为最后一个参数传入到方法中
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {

            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        },  HomeMessageDao.class,IMMessageDao.class, RemarkNameDao.class);
    }
}

