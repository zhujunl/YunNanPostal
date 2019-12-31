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
        FileUtil.deleteImg(idCardRecord.getCardPicture());
        FileUtil.deleteImg(idCardRecord.getFacePicture());
    }

    public static List<IDCardRecord> loadAll() {
        return AppDatabase.getInstance().idCardRecordDao().loadAll();
    }

}
