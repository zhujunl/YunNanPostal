package com.miaxis.postal.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.miaxis.postal.data.entity.Express;

import java.util.List;

@Dao
public interface ExpressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Express express);

    @Query("select * from Express where verifyId = :verifyId")
    List<Express> loadExpress(String verifyId);

    @Query("select * from Express")
    List<Express> loadAll();

    @Query("delete from Express")
    void deleteAll();

    @Delete
    void delete(Express express);

}
