package com.miaxis.postal.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.miaxis.postal.data.entity.IDCard;

@Dao
public interface IDCardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(IDCard idCard);

    @Update
    void update(IDCard idCard);

    @Query("select * from IDCard where IDCard.cardNumber = :cardNumber")
    IDCard findIDCardByCardNumber(String cardNumber);

    @Query("select * from IDCard order by IDCard.verifyTime asc limit 1")
    IDCard findOldestIDCard();

    @Query("select count(*) from IDCard")
    int loadIDCardCount();

    @Delete
    void delete(IDCard idCard);

}
