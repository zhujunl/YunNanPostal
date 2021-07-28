package com.miaxis.postal.data.model;

import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.data.entity.Courier;

public class CourierModel {

    public static void saveCourier(Courier courier) {
        courier.setId(1L);
        AppDatabase.getInstance().courierDao().deleteAll();
        courier.setLogin(false);
        AppDatabase.getInstance().courierDao().insert(courier);
    }

    public static void setLogin() {
        Courier courier = loadCourier();
        if (courier != null) {
            courier.setLogin(true);
            AppDatabase.getInstance().courierDao().updateCourier(courier);
        }
    }

    public static void setLoginOut() {
        Courier courier = loadCourier();
        if (courier != null) {
            courier.setLogin(false);
            AppDatabase.getInstance().courierDao().updateCourier(courier);
        }
    }

    public static Courier loadCourier() {
        return AppDatabase.getInstance().courierDao().loadCourier();
    }

}
