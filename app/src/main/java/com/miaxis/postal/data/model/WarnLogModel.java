package com.miaxis.postal.data.model;

import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.data.entity.WarnLog;

import java.util.List;

public class WarnLogModel {

    public static void saveWarnLog(WarnLog warnLog) {
        AppDatabase.getInstance().warnLogDao().insert(warnLog);
    }

    public static void deleteWarnLog(WarnLog warnLog) {
        AppDatabase.getInstance().warnLogDao().delete(warnLog);
    }

    public static List<WarnLog> loadAll() {
        return AppDatabase.getInstance().warnLogDao().loadAll();
    }

    public static int loadWarnLogCount() {
        return AppDatabase.getInstance().warnLogDao().loadWarnLogCount();
    }

    public static WarnLog findOldestWarnLog() {
        return AppDatabase.getInstance().warnLogDao().findOldestWarnLog();
    }

}
