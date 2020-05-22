package com.miaxis.postal.data.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.miaxis.postal.data.entity.WarnLog;

import java.util.List;

public interface WarnLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WarnLog warnLog);

    @Query("select * from WarnLog")
    List<WarnLog> loadAll();

    @Query("delete from WarnLog")
    void deleteAll();

    @Delete
    void delete(WarnLog warnLog);

}
