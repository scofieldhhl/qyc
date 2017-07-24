/*
******************************* Copyright (c)*********************************\
**
**                 (c) Copyright 2015, 蒋朋, china, qd. sd
**                          All Rights Reserved
**
**                           By()
**                         
**-----------------------------------版本信息------------------------------------
** 版    本: V0.1
**
**------------------------------------------------------------------------------
********************************End of Head************************************\
*/

package com.systemteam.database.db;


import com.systemteam.dao.BikeInfoDao;
import com.systemteam.dao.RouteRecordDao;

/**
 * @Description 获取表 Helper 的工具类
 * @author scofield.hhl@gmail.com
 * @time 2016/12/2
 */
public class DbUtil {
    private static BikeInfoDBHelper sBikeHelper;

    private static BikeInfoDao getBikeInfoDao() {
        return DbCore.getDaoSession().getBikeInfoDao();
    }

    public static BikeInfoDBHelper getBikeInfoHelper() {
        if (sBikeHelper == null) {
            sBikeHelper = new BikeInfoDBHelper(getBikeInfoDao());
        }
        return sBikeHelper;
    }

    private static RouteRecordDBHelper sRouteHelper;
    private static RouteRecordDao getRouteRecordDao() {
        return DbCore.getDaoSession().getRouteRecordDao();
    }

    public static RouteRecordDBHelper getRouteRecordDaoHelper() {
        if (sRouteHelper == null) {
            sRouteHelper = new RouteRecordDBHelper(getRouteRecordDao());
        }
        return sRouteHelper;
    }


}
