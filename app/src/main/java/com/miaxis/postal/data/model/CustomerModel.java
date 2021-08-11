package com.miaxis.postal.data.model;

import android.text.TextUtils;

import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.data.entity.Customer;
import com.miaxis.postal.util.ListUtils;

import java.util.List;

public class CustomerModel {

    public static void save(Customer customer) {
        if (customer == null || TextUtils.isEmpty(customer.name)) {
            return;
        }
        Customer byName = findByName(customer.belong, customer.name);
        if (byName != null) {
            customer.id = byName.id;
        }
        AppDatabase.getInstance().customerDao().save(customer);
    }

    public static void delete(Customer customer) {
        AppDatabase.getInstance().customerDao().delete(customer);
    }

    public static List<Customer> find(String expressMan) {
        if (TextUtils.isEmpty(expressMan)) {
            return null;
        }
        return AppDatabase.getInstance().customerDao().find(expressMan);
    }

    public synchronized static Customer findByName(String expressMan, String name) {
        if (TextUtils.isEmpty(expressMan) || TextUtils.isEmpty(name)) {
            return null;
        }
        List<Customer> byName = AppDatabase.getInstance().customerDao().findByName(expressMan, name);
        if (ListUtils.isNullOrEmpty(byName)) {
            return null;
        }
        return byName.get(0);
    }

}
