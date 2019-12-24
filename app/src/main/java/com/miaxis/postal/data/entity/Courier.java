package com.miaxis.postal.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Courier {

    @PrimaryKey
    private long id;
    private String name;
    private String cardNo;
    private String phone;
    private String photo;
    private String faceFeature;
    private String finger1Feature;
    private String finger2Feature;
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

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
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

    public String getFinger1Feature() {
        return finger1Feature;
    }

    public void setFinger1Feature(String finger1Feature) {
        this.finger1Feature = finger1Feature;
    }

    public String getFinger2Feature() {
        return finger2Feature;
    }

    public void setFinger2Feature(String finger2Feature) {
        this.finger2Feature = finger2Feature;
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
        private String cardNo;
        private String phone;
        private String photo;
        private String faceFeature;
        private String finger1Feature;
        private String finger2Feature;
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

        public CourierBuilder cardNo(String cardNo) {
            this.cardNo = cardNo;
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

        public CourierBuilder finger1Feature(String finger1Feature) {
            this.finger1Feature = finger1Feature;
            return this;
        }

        public CourierBuilder finger2Feature(String finger2Feature) {
            this.finger2Feature = finger2Feature;
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
            courier.setCardNo(cardNo);
            courier.setPhone(phone);
            courier.setPhoto(photo);
            courier.setFaceFeature(faceFeature);
            courier.setFinger1Feature(finger1Feature);
            courier.setFinger2Feature(finger2Feature);
            courier.setCreateTime(createTime);
            return courier;
        }
    }
}
