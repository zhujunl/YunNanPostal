package com.miaxis.postal.data.model;

import android.text.TextUtils;

import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.data.entity.Customer;
import com.miaxis.postal.util.ListUtils;

import java.util.List;

public class CustomerModel {

    public static void save(Customer customer) {
        if (customer == null || TextUtils.isEmpty(customer.phone)) {
            return;
        }
        Customer byPhone = findByPhone(customer.expressmanId, customer.phone);
        if (byPhone != null) {
            customer.id = byPhone.id;
        }
        customer.orderNumber++;
        AppDatabase.getInstance().customerDao().save(customer);
    }

    public static void delete(Customer customer) {
        AppDatabase.getInstance().customerDao().delete(customer);
    }

    public static void delete(String expressMan) {
        AppDatabase.getInstance().customerDao().delete(expressMan);
    }

    public static List<Customer> find(String expressMan) {
        if (TextUtils.isEmpty(expressMan)) {
            return null;
        }
        return AppDatabase.getInstance().customerDao().find(expressMan);
    }

    public synchronized static Customer findByPhone(String expressMan, String phone) {
        if (TextUtils.isEmpty(expressMan) || TextUtils.isEmpty(phone)) {
            return null;
        }
        List<Customer> byName = AppDatabase.getInstance().customerDao().findByPhone(expressMan, phone);
        if (ListUtils.isNullOrEmpty(byName)) {
            return null;
        }
        return byName.get(0);
    }

    //    public synchronized static Customer findByName(String expressMan, String name) {
    //        if (TextUtils.isEmpty(expressMan) || TextUtils.isEmpty(name)) {
    //            return null;
    //        }
    //        List<Customer> byName = AppDatabase.getInstance().customerDao().findByName(expressMan, name);
    //        if (ListUtils.isNullOrEmpty(byName)) {
    //            return null;
    //        }
    //        return byName.get(0);
    //    }

}
