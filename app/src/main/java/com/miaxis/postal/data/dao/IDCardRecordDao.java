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
    IDCardRecord loadIDCardRecord(String verifyId);

    @Query("select * from IDCardRecord")
    List<IDCardRecord> loadAll();

    @Query("select * from IDCardRecord where IDCardRecord.upload = 0 order by IDCardRecord.verifyTime asc limit 1")
    IDCardRecord findOldestIDCardRecord();

    @Query("delete from IDCardRecord")
    void deleteAll();

    @Delete
    void delete(IDCardRecord idCardRecord);

}
