package com.miaxis.postal.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author Tank
 * @date 2021/8/11 10:52 上午
 * @des
 * @updateAuthor
 * @updateDes
 */

@Entity
public class Customer {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String expressmanId = "";
    public String name = "";
    public String phone = "";
    public int orderNumber;

    public Customer(String expressmanId, String name, String phone) {
        this.expressmanId = expressmanId;
        this.name = name;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", belong='" + expressmanId + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", phone='" + orderNumber + '\'' +
                '}';
    }
}
