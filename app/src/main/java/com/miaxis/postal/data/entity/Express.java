package com.miaxis.postal.data.entity;

import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.miaxis.postal.bridge.Status;

import java.util.Date;
import java.util.List;

@Entity
public class Express {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String barCode;
    private List<String> photoPathList;
    private String senderPhone;
    private String senderAddress;
    private String verifyId;
    private String latitude;
    private String longitude;
    private Date pieceTime;
    private boolean upload;

    @Ignore
    private List<Bitmap> photoList;

    public Express() {
    }

    private Express(Builder builder) {
        setId(builder.id);
        setBarCode(builder.barCode);
        setPhotoPathList(builder.photoPathList);
        setSenderPhone(builder.senderPhone);
        setSenderAddress(builder.senderAddress);
        setVerifyId(builder.verifyId);
        setLatitude(builder.latitude);
        setLongitude(builder.longitude);
        setPieceTime(builder.pieceTime);
        setUpload(builder.upload);
        setPhotoList(builder.photoList);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public List<String> getPhotoPathList() {
        return photoPathList;
    }

    public void setPhotoPathList(List<String> photoPathList) {
        this.photoPathList = photoPathList;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getVerifyId() {
        return verifyId;
    }

    public void setVerifyId(String verifyId) {
        this.verifyId = verifyId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Date getPieceTime() {
        return pieceTime;
    }

    public void setPieceTime(Date pieceTime) {
        this.pieceTime = pieceTime;
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public List<Bitmap> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<Bitmap> photoList) {
        this.photoList = photoList;
    }

    public static final class Builder {
        private Long id;
        private String barCode;
        private List<String> photoPathList;
        private String senderPhone;
        private String senderAddress;
        private String verifyId;
        private String latitude;
        private String longitude;
        private Date pieceTime;
        private boolean upload;
        private List<Bitmap> photoList;

        public Builder() {
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder barCode(String val) {
            barCode = val;
            return this;
        }

        public Builder photoPathList(List<String> val) {
            photoPathList = val;
            return this;
        }

        public Builder senderPhone(String val) {
            senderPhone = val;
            return this;
        }

        public Builder senderAddress(String val) {
            senderAddress = val;
            return this;
        }

        public Builder verifyId(String val) {
            verifyId = val;
            return this;
        }

        public Builder latitude(String val) {
            latitude = val;
            return this;
        }

        public Builder longitude(String val) {
            longitude = val;
            return this;
        }

        public Builder pieceTime(Date val) {
            pieceTime = val;
            return this;
        }

        public Builder upload(boolean val) {
            upload = val;
            return this;
        }

        public Builder photoList(List<Bitmap> val) {
            photoList = val;
            return this;
        }

        public Express build() {
            return new Express(this);
        }
    }
}
