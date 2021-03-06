package com.ityun.zhihuiyun.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.ityun.zhihuiyun.bean.HomeMessage;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "HOME_MESSAGE".
*/
public class HomeMessageDao extends AbstractDao<HomeMessage, Long> {

    public static final String TABLENAME = "HOME_MESSAGE";

    /**
     * Properties of entity HomeMessage.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property HomeId = new Property(0, Long.class, "homeId", true, "_id");
        public final static Property UsrId = new Property(1, int.class, "usrId", false, "USR_ID");
        public final static Property UserName = new Property(2, String.class, "userName", false, "USER_NAME");
        public final static Property Id = new Property(3, int.class, "id", false, "ID");
        public final static Property Name = new Property(4, String.class, "name", false, "NAME");
        public final static Property ImType = new Property(5, int.class, "imType", false, "IM_TYPE");
        public final static Property Time = new Property(6, long.class, "time", false, "TIME");
        public final static Property Content = new Property(7, String.class, "content", false, "CONTENT");
        public final static Property MessageType = new Property(8, int.class, "messageType", false, "MESSAGE_TYPE");
    }


    public HomeMessageDao(DaoConfig config) {
        super(config);
    }
    
    public HomeMessageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"HOME_MESSAGE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: homeId
                "\"USR_ID\" INTEGER NOT NULL ," + // 1: usrId
                "\"USER_NAME\" TEXT," + // 2: userName
                "\"ID\" INTEGER NOT NULL ," + // 3: id
                "\"NAME\" TEXT," + // 4: name
                "\"IM_TYPE\" INTEGER NOT NULL ," + // 5: imType
                "\"TIME\" INTEGER NOT NULL ," + // 6: time
                "\"CONTENT\" TEXT," + // 7: content
                "\"MESSAGE_TYPE\" INTEGER NOT NULL );"); // 8: messageType
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"HOME_MESSAGE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, HomeMessage entity) {
        stmt.clearBindings();
 
        Long homeId = entity.getHomeId();
        if (homeId != null) {
            stmt.bindLong(1, homeId);
        }
        stmt.bindLong(2, entity.getUsrId());
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(3, userName);
        }
        stmt.bindLong(4, entity.getId());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
        stmt.bindLong(6, entity.getImType());
        stmt.bindLong(7, entity.getTime());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(8, content);
        }
        stmt.bindLong(9, entity.getMessageType());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, HomeMessage entity) {
        stmt.clearBindings();
 
        Long homeId = entity.getHomeId();
        if (homeId != null) {
            stmt.bindLong(1, homeId);
        }
        stmt.bindLong(2, entity.getUsrId());
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(3, userName);
        }
        stmt.bindLong(4, entity.getId());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
        stmt.bindLong(6, entity.getImType());
        stmt.bindLong(7, entity.getTime());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(8, content);
        }
        stmt.bindLong(9, entity.getMessageType());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public HomeMessage readEntity(Cursor cursor, int offset) {
        HomeMessage entity = new HomeMessage( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // homeId
            cursor.getInt(offset + 1), // usrId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // userName
            cursor.getInt(offset + 3), // id
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // name
            cursor.getInt(offset + 5), // imType
            cursor.getLong(offset + 6), // time
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // content
            cursor.getInt(offset + 8) // messageType
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, HomeMessage entity, int offset) {
        entity.setHomeId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUsrId(cursor.getInt(offset + 1));
        entity.setUserName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setId(cursor.getInt(offset + 3));
        entity.setName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setImType(cursor.getInt(offset + 5));
        entity.setTime(cursor.getLong(offset + 6));
        entity.setContent(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setMessageType(cursor.getInt(offset + 8));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(HomeMessage entity, long rowId) {
        entity.setHomeId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(HomeMessage entity) {
        if(entity != null) {
            return entity.getHomeId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(HomeMessage entity) {
        return entity.getHomeId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
