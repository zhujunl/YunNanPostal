package com.miaxis.postal.data.dao;

import com.miaxis.postal.data.entity.Branch;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface BranchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Branch branch);

    @Query("select * from Branch  order by Branch.id")
    List<Branch> find();

    @Query("select * from Branch where Branch.isSelected = 1 order by Branch.id")
    List<Branch> findSelected();

    @Query("select count(*) from Branch ")
    int allCount();

    @Query("select * from Branch order by Branch.id")
    Branch findBranch();

    @Query("delete from Branch")
    void deleteAll();

    @Delete
    void delete(Branch branch);

}
