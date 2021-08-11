package com.miaxis.postal.data.dao;

import com.miaxis.postal.data.entity.Customer;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Customer customer);

    @Query("select * from Customer where Customer.belong=:expressMan order by Customer.id")
    List<Customer> find(String expressMan);

    @Query("select * from Customer where Customer.belong=:expressMan and Customer.name=:name order by Customer.id")
    List<Customer> findByName(String expressMan, String name);

    @Delete
    void delete(Customer customer);

}
