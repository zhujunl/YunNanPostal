package com.miaxis.postal.data.entity;

import android.graphics.Bitmap;

public class Photograph {

    private Bitmap bitmap;
    private boolean select;
    private boolean local;

    public Photograph() {
    }

    public Photograph(Bitmap bitmap, boolean select) {
        this.bitmap = bitmap;
        this.select = select;
    }

    public Photograph(Bitmap bitmap, boolean select, boolean local) {
        this.bitmap = bitmap;
        this.select = select;
        this.local = local;
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

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }
}
