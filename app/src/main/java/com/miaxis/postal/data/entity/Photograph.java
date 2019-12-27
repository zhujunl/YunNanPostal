package com.miaxis.postal.data.entity;

import android.graphics.Bitmap;

public class Photograph {

    private Bitmap bitmap;
    private boolean select;

    public Photograph() {
    }

    public Photograph(Bitmap bitmap, boolean select) {
        this.bitmap = bitmap;
        this.select = select;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
