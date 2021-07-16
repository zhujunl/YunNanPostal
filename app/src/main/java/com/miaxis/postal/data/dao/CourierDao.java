package com.miaxis.postal.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.miaxis.postal.data.entity.Courier;

@Dao
public interface CourierDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Courier courier);

    @Query("select * from courier where id = 1")
    Courier loadCourier();

    @Update
    int updateCourier(Courier courier);

    @Query("delete from courier")
    void deleteAll();

}
