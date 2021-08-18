package com.miaxis.postal.data.entity;

import java.util.List;

public class Order {

    private int id;
    private int personId;
    private int checkId;
    private String senderAddress;
    private String senderPhone;
    private String senderName;
    private String orderCode;
    private String orderInfo;
    private String latitude;
    private String longitude;
    private String addresseeName;
    private String addresseeAddress;
    private String addresseePhone;
    private String weight;
    private String pieceTime;
    private String checkImage;
    private String cardImage;
    private List<String> imageList;
    private String receiptTime;
    private String createTime;
    private String goodsName;

    public Order() {
    }

    private Order(Builder builder) {
        setId(builder.id);
        setPersonId(builder.personId);
        setCheckId(builder.checkId);
        setSenderAddress(builder.senderAddress);
        setSenderPhone(builder.senderPhone);
        setSenderName(builder.senderName);
        setOrderCode(builder.orderCode);
        setOrderInfo(builder.orderInfo);
        setLatitude(builder.latitude);
        setLongitude(builder.longitude);
        setAddresseeName(builder.addresseeName);
        setAddresseeAddress(builder.addresseeAddress);
        setAddresseePhone(builder.addresseePhone);
        setWeight(builder.weight);
        setPieceTime(builder.pieceTime);
        setCheckImage(builder.checkImage);
        setCardImage(builder.cardImage);
        setImageList(builder.imageList);
        setReceiptTime(builder.receiptTime);
        setCreateTime(builder.createTime);
        setGoodsName(builder.goodsName);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public int getCheckId() {
        return checkId;
    }

    public void setCheckId(int checkId) {
        this.checkId = checkId;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
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

    public String getAddresseeName() {
        return addresseeName;
    }

    public void setAddresseeName(String addresseeName) {
        this.addresseeName = addresseeName;
    }

    public String getAddresseeAddress() {
        return addresseeAddress;
    }

    public void setAddresseeAddress(String addresseeAddress) {
        this.addresseeAddress = addresseeAddress;
    }

    public String getAddresseePhone() {
        return addresseePhone;
    }

    public void setAddresseePhone(String addresseePhone) {
        this.addresseePhone = addresseePhone;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPieceTime() {
        return pieceTime;
    }

    public void setPieceTime(String pieceTime) {
        this.pieceTime = pieceTime;
    }

    public String getCheckImage() {
        return checkImage;
    }

    public void setCheckImage(String checkImage) {
        this.checkImage = checkImage;
    }

    public String getCardImage() {
        return cardImage;
    }

    public void setCardImage(String cardImage) {
        this.cardImage = cardImage;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public String getReceiptTime() {
        return receiptTime;
    }

    public void setReceiptTime(String receiptTime) {
        this.receiptTime = receiptTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", personId=" + personId +
                ", checkId=" + checkId +
                ", senderAddress='" + senderAddress + '\'' +
                ", senderPhone='" + senderPhone + '\'' +
                ", senderName='" + senderName + '\'' +
                ", orderCode='" + orderCode + '\'' +
                ", orderInfo='" + orderInfo + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", addresseeName='" + addresseeName + '\'' +
                ", addresseeAddress='" + addresseeAddress + '\'' +
                ", addresseePhone='" + addresseePhone + '\'' +
                ", weight='" + weight + '\'' +
                ", pieceTime='" + pieceTime + '\'' +
                ", checkImage='" + checkImage + '\'' +
                ", cardImage='" + cardImage + '\'' +
                ", imageList=" + imageList +
                ", receiptTime='" + receiptTime + '\'' +
                ", createTime='" + createTime + '\'' +
                ", goodsName='" + goodsName + '\'' +
                '}';
    }

    public static final class Builder {
        private int id;
        private int personId;
        private int checkId;
        private String senderAddress;
        private String senderPhone;
        private String senderName;
        private String orderCode;
        private String orderInfo;
        private String latitude;
        private String longitude;
        private String addresseeName;
        private String addresseeAddress;
        private String addresseePhone;
        private String weight;
        private String pieceTime;
        private String checkImage;
        private String cardImage;
        private List<String> imageList;
        private String receiptTime;
        private String createTime;
        private String goodsName;

        public Builder() {
        }

        public Builder id(int val) {
            id = val;
            return this;
        }

        public Builder personId(int val) {
            personId = val;
            return this;
        }

        public Builder checkId(int val) {
            checkId = val;
            return this;
        }

        public Builder senderAddress(String val) {
            senderAddress = val;
            return this;
        }

        public Builder senderPhone(String val) {
            senderPhone = val;
            return this;
        }

        public Builder senderName(String val) {
            senderName = val;
            return this;
        }

        public Builder orderCode(String val) {
            orderCode = val;
            return this;
        }

        public Builder orderInfo(String val) {
            orderInfo = val;
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

        public Builder addresseeName(String val) {
            addresseeName = val;
            return this;
        }

        public Builder addresseeAddress(String val) {
            addresseeAddress = val;
            return this;
        }

        public Builder addresseePhone(String val) {
            addresseePhone = val;
            return this;
        }

        public Builder weight(String val) {
            weight = val;
            return this;
        }

        public Builder pieceTime(String val) {
            pieceTime = val;
            return this;
        }

        public Builder checkImage(String val) {
            checkImage = val;
            return this;
        }

        public Builder cardImage(String val) {
            cardImage = val;
            return this;
        }

        public Builder imageList(List<String> val) {
            imageList = val;
            return this;
        }

        public Builder receiptTime(String val) {
            receiptTime = val;
            return this;
        }

        public Builder createTime(String val) {
            createTime = val;
            return this;
        }

        public Builder setGoodsName(String goodsName) {
            this.goodsName = goodsName;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
