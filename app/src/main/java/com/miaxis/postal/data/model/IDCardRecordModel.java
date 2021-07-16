package com.miaxis.postal.data.model;

import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.util.FileUtil;

import java.util.List;

public class IDCardRecordModel {

    public static void saveIDCardRecord(IDCardRecord idCardRecord) {
        AppDatabase.getInstance().idCardRecordDao().insert(idCardRecord);
    }

    public static IDCardRecord loadIDCardRecordByVerifyId(String verifyId) {
        return AppDatabase.getInstance().idCardRecordDao().loadIDCardRecordByVerifyId(verifyId);
    }

    public static IDCardRecord loadIDCardRecordById(Long id) {
        return AppDatabase.getInstance().idCardRecordDao().loadIDCardRecordById(id);
    }

    public static void deleteIDCardRecord(IDCardRecord idCardRecord) {
        AppDatabase.getInstance().idCardRecordDao().delete(idCardRecord);
    }

    public static List<IDCardRecord> loadAllNotDraft() {
        return AppDatabase.getInstance().idCardRecordDao().loadAllNotDraft();
    }

    public static IDCardRecord findOldestIDCardRecord() {
        return AppDatabase.getInstance().idCardRecordDao().findOldestIDCardRecord();
    }

    public static List<IDCardRecord> loadDraftIDCardRecordByPage(int pageNum, int pageSize) {
        return AppDatabase.getInstance().idCardRecordDao().loadDraftIDCardRecordByPage(pageNum, pageSize);
    }


}
