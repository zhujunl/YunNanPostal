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
    private String fingerFeature1;
    private String fingerFeature2;
    private String createTime;

    public Courier() {
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

    public static final class CourierBuilder {
        private long id;
        private String name;
        private String cardNumber;
        private String phone;
        private String photo;
        private String faceFeature;
        private String fingerFeature1;
        private String fingerFeature2;
        private String createTime;

        private CourierBuilder() {
        }

        public static CourierBuilder aCourier() {
            return new CourierBuilder();
        }

        public CourierBuilder id(long id) {
            this.id = id;
            return this;
        }

        public CourierBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CourierBuilder cardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public CourierBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public CourierBuilder photo(String photo) {
            this.photo = photo;
            return this;
        }

        public CourierBuilder faceFeature(String faceFeature) {
            this.faceFeature = faceFeature;
            return this;
        }

        public CourierBuilder fingerFeature1(String fingerFeature1) {
            this.fingerFeature1 = fingerFeature1;
            return this;
        }

        public CourierBuilder fingerFeature2(String fingerFeature2) {
            this.fingerFeature2 = fingerFeature2;
            return this;
        }

        public CourierBuilder createTime(String createTime) {
            this.createTime = createTime;
            return this;
        }

        public Courier build() {
            Courier courier = new Courier();
            courier.setId(id);
            courier.setName(name);
            courier.setCardNumber(cardNumber);
            courier.setPhone(phone);
            courier.setPhoto(photo);
            courier.setFaceFeature(faceFeature);
            courier.setFingerFeature1(fingerFeature1);
            courier.setFingerFeature2(fingerFeature2);
            courier.setCreateTime(createTime);
            return courier;
        }
    }
}
