

package com.systemteam.database.db;

import android.content.Context;

import com.systemteam.dao.BikeInfoDao;
import com.systemteam.dao.DaoMaster;
import com.systemteam.util.LogTool;

import org.greenrobot.greendao.database.Database;


/**
 * @Description 升级
 * @author scofield.hhl@gmail.com
 * @time 2016/12/2
 */
public class MyOpenHelper extends DaoMaster.OpenHelper {
    public MyOpenHelper(Context context, String name) {
        super(context, name);
    }


    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        LogTool.w("db version update from " + oldVersion + " to " + newVersion);

        BikeInfoDao.createTable(db, true);
        for (int i = oldVersion; i < newVersion; i++) {
            switch (i) {
                case 1:
                    // 加入新字段
//                    db.execSQL("ALTER TABLE 'TASKS_MANAGER_MODEL' ADD 'FORMAT' TEXT;");
                    break;
                case 2:
//                    db.execSQL("ALTER TABLE 'TASKS_MANAGER_MODEL' ADD 'PATH_REFORMAT' TEXT;");
                    break;
                case 3:
//                    db.execSQL("ALTER TABLE 'TASKS_MANAGER_MODEL' ADD 'TIME' INTEGER;");
                    break;
            }
        }
    }
}
