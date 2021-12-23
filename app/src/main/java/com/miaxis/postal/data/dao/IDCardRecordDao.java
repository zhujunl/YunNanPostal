package com.miaxis.postal.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.miaxis.postal.data.entity.IDCardRecord;

import java.util.List;

@Dao
public interface IDCardRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(IDCardRecord idCardRecord);

    @Query("select * from IDCardRecord where verifyId = :verifyId")
    IDCardRecord loadIDCardRecordByVerifyId(String verifyId);

    @Query("select * from IDCardRecord where IDCardRecord.id = :id")
    IDCardRecord loadIDCardRecordById(Long id);

    @Query("select * from IDCardRecord where IDCardRecord.draft = 0")
    List<IDCardRecord> loadAllNotDraft();

    @Query("select * from IDCardRecord where IDCardRecord.upload = 0 and IDCardRecord.draft = 0 order by IDCardRecord.verifyTime asc ")
    List<IDCardRecord> findOldestIDCardRecord();

    @Query("select * from IDCardRecord where IDCardRecord.draft = 1 order by IDCardRecord.verifyTime asc limit :pageSize offset :pageSize * (:pageNum - 1)")
    List<IDCardRecord> loadDraftIDCardRecordByPage(int pageNum, int pageSize);

    @Query("delete from IDCardRecord")
    void deleteAll();

    @Delete
    void delete(IDCardRecord idCardRecord);

}
