package com.miaxis.postal.data.event;

import android.graphics.Bitmap;

import java.util.List;

public class TakePhotoEvent {

    private List<Bitmap> photoList;

    public TakePhotoEvent(List<Bitmap> photoList) {
        this.photoList = photoList;
    }

    public List<Bitmap> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<Bitmap> photoList) {
        this.photoList = photoList;
    }

    @Override
    public String toString() {
        return "TakePhotoEvent{" +
                "photoList=" + photoList +
                '}';
    }
}
