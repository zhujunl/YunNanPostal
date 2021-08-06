package com.miaxis.postal.data.entity;


import com.miaxis.postal.util.ListUtils;

import java.util.List;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Branch {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String orgName;
    public String orgCode;
    public String orgNode;
    public boolean isSelected = false;

    public Branch() {
    }

    public boolean isEmpty() {
        return orgCode == null || orgNode == null || orgName == null;
    }

    public static Branch findSelected(List<Branch> list) {
        if (ListUtils.isNullOrEmpty(list)) {
            return null;
        }
        for (Branch branch : list) {
            if (branch.isSelected) {
                return branch;
            }
        }
        return null;
    }

    public static int findSelectedPosition(List<Branch> list) {
        if (ListUtils.isNullOrEmpty(list)) {
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            Branch branch = list.get(i);
            if (branch.isSelected) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Branch{" +
                "id=" + id +
                ", orgName='" + orgName + '\'' +
                ", orgCode='" + orgCode + '\'' +
                ", orgNode='" + orgNode + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }

}
