package com.miaxis.postal.data.entity;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.List;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

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
    private String uploadError;
    private String info;
    private String weight;
    private String addresseeName;
    private String addresseePhone;
    private String addresseeAddress;
    private boolean draft;
    private boolean complete;
    private String phone;
    private String orgCode;//机构号1
    private String orgNode;//机构号2
    private String customerName;
    private String customerPhone;
    private String goodsName;
    private String goodsNumber;
    private String customerType = "1";

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
        setUploadError(builder.uploadError);
        setInfo(builder.info);
        setWeight(builder.weight);
        setAddresseeName(builder.addresseeName);
        setAddresseePhone(builder.addresseePhone);
        setAddresseeAddress(builder.addresseeAddress);
        setDraft(builder.draft);
        setPhotoList(builder.photoList);
        setComplete(builder.complete);
        setPhone(builder.phone);
        setOrgCode(builder.orgCode);
        setOrgNode(builder.orgNode);
        setCustomerName(builder.customerName);
        setGoodsName(builder.goodsName);
        setGoodsNumber(builder.goodsNumber);
        setCustomerType(builder.customerType);
        setCustomerPhone(builder.customerPhone);
    }

    public String getOrgNode() {
        return orgNode;
    }

    public void setOrgNode(String orgNode) {
        this.orgNode = orgNode;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUploadError(String uploadError) {
        this.uploadError = uploadError;
    }

    public String getUploadError() {
        return uploadError;
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAddresseeName() {
        return addresseeName;
    }

    public void setAddresseeName(String addresseeName) {
        this.addresseeName = addresseeName;
    }

    public String getAddresseePhone() {
        return addresseePhone;
    }

    public void setAddresseePhone(String addresseePhone) {
        this.addresseePhone = addresseePhone;
    }

    public String getAddresseeAddress() {
        return addresseeAddress;
    }

    public void setAddresseeAddress(String addresseeAddress) {
        this.addresseeAddress = addresseeAddress;
    }

    public List<Bitmap> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<Bitmap> photoList) {
        this.photoList = photoList;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsNumber() {
        return goodsNumber;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public void setGoodsNumber(String goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    @Override
    public String toString() {
        return "Express{" +
                "id=" + id +
                ", phone='" + phone + '\'' +
                ", barCode='" + barCode + '\'' +
                ", photoPathList=" + photoPathList +
                ", senderPhone='" + senderPhone + '\'' +
                ", senderAddress='" + senderAddress + '\'' +
                ", verifyId='" + verifyId + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", pieceTime=" + pieceTime +
                ", upload=" + upload +
                ", uploadError='" + uploadError + '\'' +
                ", info='" + info + '\'' +
                ", weight='" + weight + '\'' +
                ", addresseeName='" + addresseeName + '\'' +
                ", addresseePhone='" + addresseePhone + '\'' +
                ", addresseeAddress='" + addresseeAddress + '\'' +
                ", draft=" + draft +
                ", complete=" + complete +
                ", photoList=" + photoList +
                '}';
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
        private String uploadError;
        private String info;
        private String weight;
        private String addresseeName;
        private String addresseePhone;
        private String addresseeAddress;
        private boolean draft;
        private List<Bitmap> photoList;
        private boolean complete;
        private String phone;
        private String orgCode;//机构号
        private String orgNode;
        private String customerName;
        private String goodsName;
        private String goodsNumber;
        private String customerType = "1";
        private String customerPhone;

        public Builder() {
        }

        public Builder orgNode(String val) {
            orgNode = val;
            return this;
        }

        public Builder orgCode(String val) {
            orgCode = val;
            return this;
        }

        public Builder phone(String val) {
            phone = val;
            return this;
        }

        public Builder uploadError(String val) {
            uploadError = val;
            return this;
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

        public Builder info(String val) {
            info = val;
            return this;
        }

        public Builder weight(String val) {
            weight = val;
            return this;
        }

        public Builder addresseeName(String val) {
            addresseeName = val;
            return this;
        }

        public Builder addresseePhone(String val) {
            addresseePhone = val;
            return this;
        }

        public Builder addresseeAddress(String val) {
            addresseeAddress = val;
            return this;
        }

        public Builder draft(boolean val) {
            draft = val;
            return this;
        }

        public Builder photoList(List<Bitmap> val) {
            photoList = val;
            return this;
        }

        public Builder complete(boolean val) {
            complete = val;
            return this;
        }

        public Builder setCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public Builder setGoodsName(String goodsName) {
            this.goodsName = goodsName;
            return this;
        }

        public Builder setGoodsNumber(String goodsNumber) {
            this.goodsNumber = goodsNumber;
            return this;
        }

        public Builder setCustomerType(String customerType) {
            this.customerType = customerType;
            return this;
        }

        public Builder setCustomerPhone(String customerPhone) {
            this.customerPhone = customerPhone;
            return this;
        }

        public Express build() {
            return new Express(this);
        }
    }
}
