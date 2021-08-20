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

    @Query("select * from Customer where Customer.expressmanId=:expressMan order by Customer.orderNumber desc")
    List<Customer> find(String expressMan);

    //    @Query("select * from Customer where Customer.expressmanId=:expressMan and Customer.name=:name order by Customer.id")
    //    List<Customer> findByName(String expressMan, String name);

    @Query("select * from Customer where Customer.expressmanId=:expressMan and Customer.phone=:phone order by Customer.orderNumber desc")
    List<Customer> findByPhone(String expressMan, String phone);

    @Delete
    void delete(Customer customer);

    @Query("delete from Customer where Customer.expressmanId=:expressMan")
    void delete(String expressMan);

}
