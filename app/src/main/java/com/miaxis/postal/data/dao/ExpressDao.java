package com.miaxis.postal.data.dao;

import com.miaxis.postal.data.entity.Express;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ExpressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Express express);

    @Query("select * from Express where Express.phone = :phone and Express.draft = 0 order by Express.id desc limit :pageSize offset :pageSize * (:pageNum - 1)")
    List<Express> loadExpressByPage(String phone, int pageNum, int pageSize);

    @Query("select * from Express where Express.phone = :phone and verifyId = :verifyId")
    List<Express> loadExpress(String phone, String verifyId);

    @Query("select * from Express where Express.phone = :phone")
    List<Express> loadAll(String phone);

    @Query("select * from Express where Express.phone = :phone and Express.barCode = :code")
    Express loadExpressWithCode(String phone, String code);

    @Query("select count(*) from Express where Express.phone = :phone")
    int loadExpressCount(String phone);

    @Query("select count(*) from Express")
    int loadExpressAllCount();

    @Query("delete from Express where Express.phone = :phone")
    void deleteAll(String phone);

    @Delete
    void delete(Express express);

}
