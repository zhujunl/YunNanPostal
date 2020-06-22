package com.miaxis.postal.data.model;

import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.util.FileUtil;

import java.util.List;

public class IDCardRecordModel {

    public static void saveIDCardRecord(IDCardRecord idCardRecord) {
        AppDatabase.getInstance().idCardRecordDao().insert(idCardRecord);
    }

    public static IDCardRecord loadIDCardRecord(String verifyId) {
        return AppDatabase.getInstance().idCardRecordDao().loadIDCardRecord(verifyId);
    }

    public static void deleteIDCardRecord(IDCardRecord idCardRecord) {
        AppDatabase.getInstance().idCardRecordDao().delete(idCardRecord);
    }

    public static List<IDCardRecord> loadAll() {
        return AppDatabase.getInstance().idCardRecordDao().loadAll();
    }

    public static IDCardRecord findOldestIDCardRecord() {
        return AppDatabase.getInstance().idCardRecordDao().findOldestIDCardRecord();
    }

    public static List<IDCardRecord> loadDraftIDCardRecordByPage(int pageNum, int pageSize) {
        return AppDatabase.getInstance().idCardRecordDao().loadDraftIDCardRecordByPage(pageNum, pageSize);
    }

}
