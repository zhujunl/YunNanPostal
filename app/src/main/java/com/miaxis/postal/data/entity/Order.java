package com.miaxis.postal.data.entity;

import java.util.List;

public class Order {

    private String barCode;
    private List<String> photoList;

    public Order() {
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public List<String> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<String> photoList) {
        this.photoList = photoList;
    }
}
