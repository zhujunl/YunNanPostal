package com.miaxis.postal.data.model;

import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.data.entity.Branch;
import com.miaxis.postal.util.ListUtils;

import java.util.ArrayList;
import java.util.List;

public class BranchModel {

    public static void save(Branch branch) {
        AppDatabase.getInstance().branchDao().save(branch);
    }

    public static void delete(Branch branch) {
        AppDatabase.getInstance().branchDao().delete(branch);
    }

    public static void deleteAll() {
        AppDatabase.getInstance().branchDao().deleteAll();
    }

    public static List<Branch> find() {
        List<Branch> list = AppDatabase.getInstance().branchDao().find();
        if (ListUtils.isNull(list)) {
            return new ArrayList<>();
        }
        return list;
    }

    public static Branch findSelected() {
        List<Branch> list = AppDatabase.getInstance().branchDao().findSelected();
        if (ListUtils.isNullOrEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

}
