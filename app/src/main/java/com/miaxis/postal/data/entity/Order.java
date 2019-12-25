package com.miaxis.postal.data.entity;

import com.miaxis.postal.bridge.Status;

import java.util.List;

public class Order {

    private String barCode;
    private List<String> photoList;
    private Status status;

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
