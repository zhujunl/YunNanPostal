package com.miaxis.postal.data.model;

import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.data.entity.IDCard;

import java.util.List;

public class IDCardModel {

    public static void save(IDCard idCard) {
        AppDatabase.getInstance().idCardDao().insert(idCard);
    }

    public static void update(IDCard idCard) {
        AppDatabase.getInstance().idCardDao().update(idCard);
    }

    public static IDCard findIDCardByCardNumber(String cardNumber) {
        return AppDatabase.getInstance().idCardDao().findIDCardByCardNumber(cardNumber);
    }

    public static int loadIDCardCount() {
        return AppDatabase.getInstance().idCardDao().loadIDCardCount();
    }

    public static void delete(IDCard idCard) {
        AppDatabase.getInstance().idCardDao().delete(idCard);
    }

    public static IDCard findOldestIDCard() {
        return AppDatabase.getInstance().idCardDao().findOldestIDCard();
    }
    
}
