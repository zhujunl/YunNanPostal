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

    @Query("delete from IDCardRecord")
    void deleteAll();

    @Delete
    void delete(IDCardRecord idCardRecord);

}
