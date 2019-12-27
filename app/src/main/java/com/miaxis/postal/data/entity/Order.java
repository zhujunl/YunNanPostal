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
    private String pieceTime;
    private String checkImage;
    private String cardImage;
    private List<String> imageList;
    private String receiptTime;
    private String createTime;

    public Order() {
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

    public static final class OrderBuilder {
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
        private String pieceTime;
        private String checkImage;
        private String cardImage;
        private List<String> imageList;
        private String receiptTime;
        private String createTime;

        private OrderBuilder() {
        }

        public static OrderBuilder anOrder() {
            return new OrderBuilder();
        }

        public OrderBuilder id(int id) {
            this.id = id;
            return this;
        }

        public OrderBuilder personId(int personId) {
            this.personId = personId;
            return this;
        }

        public OrderBuilder checkId(int checkId) {
            this.checkId = checkId;
            return this;
        }

        public OrderBuilder senderAddress(String senderAddress) {
            this.senderAddress = senderAddress;
            return this;
        }

        public OrderBuilder senderPhone(String senderPhone) {
            this.senderPhone = senderPhone;
            return this;
        }

        public OrderBuilder senderName(String senderName) {
            this.senderName = senderName;
            return this;
        }

        public OrderBuilder orderCode(String orderCode) {
            this.orderCode = orderCode;
            return this;
        }

        public OrderBuilder orderInfo(String orderInfo) {
            this.orderInfo = orderInfo;
            return this;
        }

        public OrderBuilder latitude(String latitude) {
            this.latitude = latitude;
            return this;
        }

        public OrderBuilder longitude(String longitude) {
            this.longitude = longitude;
            return this;
        }

        public OrderBuilder addresseeName(String addresseeName) {
            this.addresseeName = addresseeName;
            return this;
        }

        public OrderBuilder addresseeAddress(String addresseeAddress) {
            this.addresseeAddress = addresseeAddress;
            return this;
        }

        public OrderBuilder addresseePhone(String addresseePhone) {
            this.addresseePhone = addresseePhone;
            return this;
        }

        public OrderBuilder pieceTime(String pieceTime) {
            this.pieceTime = pieceTime;
            return this;
        }

        public OrderBuilder checkImage(String checkImage) {
            this.checkImage = checkImage;
            return this;
        }

        public OrderBuilder cardImage(String cardImage) {
            this.cardImage = cardImage;
            return this;
        }

        public OrderBuilder imageList(List<String> imageList) {
            this.imageList = imageList;
            return this;
        }

        public OrderBuilder receiptTime(String receiptTime) {
            this.receiptTime = receiptTime;
            return this;
        }

        public OrderBuilder createTime(String createTime) {
            this.createTime = createTime;
            return this;
        }

        public Order build() {
            Order order = new Order();
            order.setId(id);
            order.setPersonId(personId);
            order.setCheckId(checkId);
            order.setSenderAddress(senderAddress);
            order.setSenderPhone(senderPhone);
            order.setSenderName(senderName);
            order.setOrderCode(orderCode);
            order.setOrderInfo(orderInfo);
            order.setLatitude(latitude);
            order.setLongitude(longitude);
            order.setAddresseeName(addresseeName);
            order.setAddresseeAddress(addresseeAddress);
            order.setAddresseePhone(addresseePhone);
            order.setPieceTime(pieceTime);
            order.setCheckImage(checkImage);
            order.setCardImage(cardImage);
            order.setImageList(imageList);
            order.setReceiptTime(receiptTime);
            order.setCreateTime(createTime);
            return order;
        }
    }
}
