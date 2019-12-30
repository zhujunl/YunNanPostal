package com.miaxis.postal.data.entity;

import android.graphics.Bitmap;

import com.miaxis.postal.bridge.Status;

import java.util.List;

public class Express {

    private String barCode;
    private List<Bitmap> photoList;
    private Status status;

    public Express() {
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public List<Bitmap> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<Bitmap> photoList) {
        this.photoList = photoList;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
