package com.miaxis.postal.data.dao;

import com.miaxis.postal.data.entity.WarnLog;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface WarnLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WarnLog warnLog);

    @Query("select * from WarnLog")
    List<WarnLog> loadAll();

    //    @Query("select count(*) from WarnLog")
    //    int loadWarnLogCount();

    @Query("select count(*) from WarnLog")
    int loadWarnLogCount();

    @Query("select * from WarnLog where WarnLog.upload = 0 order by WarnLog.createTime asc limit 1")
    WarnLog findOldestWarnLog();

    @Query("delete from WarnLog")
    void deleteAll();

    @Delete
    void delete(WarnLog warnLog);

}
