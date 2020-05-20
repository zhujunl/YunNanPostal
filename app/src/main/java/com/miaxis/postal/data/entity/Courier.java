package com.miaxis.postal.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Courier {

    @PrimaryKey
    private long id;
    private String name;
    private String cardNumber;
    private String phone;
    private String photo;
    private String faceFeature;
    private String maskFaceFeature;
    private String fingerFeature1;
    private String fingerFeature2;
    private String createTime;
    private String password;

    public Courier() {
    }

    private Courier(Builder builder) {
        setId(builder.id);
        setName(builder.name);
        setCardNumber(builder.cardNumber);
        setPhone(builder.phone);
        setPhoto(builder.photo);
        setFaceFeature(builder.faceFeature);
        setMaskFaceFeature(builder.maskFaceFeature);
        setFingerFeature1(builder.fingerFeature1);
        setFingerFeature2(builder.fingerFeature2);
        setCreateTime(builder.createTime);
        setPassword(builder.password);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(String faceFeature) {
        this.faceFeature = faceFeature;
    }

    public String getMaskFaceFeature() {
        return maskFaceFeature;
    }

    public void setMaskFaceFeature(String maskFaceFeature) {
        this.maskFaceFeature = maskFaceFeature;
    }

    public String getFingerFeature1() {
        return fingerFeature1;
    }

    public void setFingerFeature1(String fingerFeature1) {
        this.fingerFeature1 = fingerFeature1;
    }

    public String getFingerFeature2() {
        return fingerFeature2;
    }

    public void setFingerFeature2(String fingerFeature2) {
        this.fingerFeature2 = fingerFeature2;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static final class Builder {
        private long id;
        private String name;
        private String cardNumber;
        private String phone;
        private String photo;
        private String faceFeature;
        private String maskFaceFeature;
        private String fingerFeature1;
        private String fingerFeature2;
        private String createTime;
        private String password;

        public Builder() {
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder cardNumber(String val) {
            cardNumber = val;
            return this;
        }

        public Builder phone(String val) {
            phone = val;
            return this;
        }

        public Builder photo(String val) {
            photo = val;
            return this;
        }

        public Builder faceFeature(String val) {
            faceFeature = val;
            return this;
        }

        public Builder maskFaceFeature(String val) {
            maskFaceFeature = val;
            return this;
        }

        public Builder fingerFeature1(String val) {
            fingerFeature1 = val;
            return this;
        }

        public Builder fingerFeature2(String val) {
            fingerFeature2 = val;
            return this;
        }

        public Builder createTime(String val) {
            createTime = val;
            return this;
        }

        public Builder password(String val) {
            password = val;
            return this;
        }

        public Courier build() {
            return new Courier(this);
        }
    }
}
