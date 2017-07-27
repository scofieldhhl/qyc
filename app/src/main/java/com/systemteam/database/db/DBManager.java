package com.systemteam.database.db;

import com.systemteam.bean.RouteRecord;
import com.systemteam.dao.RouteRecordDao;

import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * 类描述：下载数据库管理
 * 创建人：Administrator
 * 创建时间：2016/11/9 10:39
 */

public class DBManager {

    private RouteRecordDBHelper mRouteRecordHelper;

    public DBManager() {
        mRouteRecordHelper = DbUtil.getRouteRecordDaoHelper();
    }

    public void save(RouteRecord routeRecord){
        mRouteRecordHelper.save(routeRecord);
    }

    public List<RouteRecord> getAllRouteRecord() {
        Query<RouteRecord> query = mRouteRecordHelper.queryBuilder()
                .orderDesc(RouteRecordDao.Properties.Time)
                .build();
        return query.list();
    }
}
