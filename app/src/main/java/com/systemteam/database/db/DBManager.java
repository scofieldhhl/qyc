package com.systemteam.database.db;

/**
 * 类描述：下载数据库管理
 * 创建人：Administrator
 * 创建时间：2016/11/9 10:39
 */

public class DBManager {

    private DBHelper mHelper;

    public DBManager() {
        mHelper = DbUtil.getTaskModelHelperHelper();
    }

}
