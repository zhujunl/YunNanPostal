package com.miaxis.postal.data.event;

import com.miaxis.postal.data.entity.MxRGBImage;

import org.zz.api.MXFaceInfoEx;

public class FeatureEvent {

    public final static int CAMERA_FACE = 1;
    public final static int IMAGE_FACE = 2;

    private int mode;
    private byte[] feature;
    private MXFaceInfoEx mxFaceInfoEx;
    private String message;
    private MxRGBImage mxRgbImage;
    private String mark;

    public FeatureEvent(int mode, String message, String mark) {
        this.mode = mode;
        this.message = message;
        this.mark = mark;
    }

    public FeatureEvent(int mode, MxRGBImage mxRgbImage, byte[] feature, MXFaceInfoEx mxFaceInfoEx, String mark) {
        this.mode = mode;
        this.mxRgbImage = mxRgbImage;
        this.feature = feature;
        this.mxFaceInfoEx = mxFaceInfoEx;
        this.mark = mark;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public byte[] getFeature() {
        return feature;
    }

    public void setFeature(byte[] feature) {
        this.feature = feature;
    }

    public MXFaceInfoEx getMxFaceInfoEx() {
        return mxFaceInfoEx;
    }

    public void setMxFaceInfoEx(MXFaceInfoEx mxFaceInfoEx) {
        this.mxFaceInfoEx = mxFaceInfoEx;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MxRGBImage getMxRgbImage() {
        return mxRgbImage;
    }

    public void setMxRgbImage(MxRGBImage mxRgbImage) {
        this.mxRgbImage = mxRgbImage;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
