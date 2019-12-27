package com.miaxis.postal.data.entity;

public class SimpleOrder {

    private int id;
    private String senderAddress;
    private String senderPhone;
    private String senderName;
    private String orderCode;
    private String image;

    public SimpleOrder() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static final class SimpleOrderBuilder {
        private int id;
        private String senderAddress;
        private String senderPhone;
        private String senderName;
        private String orderCode;
        private String image;

        private SimpleOrderBuilder() {
        }

        public static SimpleOrderBuilder aSimpleOrder() {
            return new SimpleOrderBuilder();
        }

        public SimpleOrderBuilder id(int id) {
            this.id = id;
            return this;
        }

        public SimpleOrderBuilder senderAddress(String senderAddress) {
            this.senderAddress = senderAddress;
            return this;
        }

        public SimpleOrderBuilder senderPhone(String senderPhone) {
            this.senderPhone = senderPhone;
            return this;
        }

        public SimpleOrderBuilder senderName(String senderName) {
            this.senderName = senderName;
            return this;
        }

        public SimpleOrderBuilder orderCode(String orderCode) {
            this.orderCode = orderCode;
            return this;
        }

        public SimpleOrderBuilder image(String image) {
            this.image = image;
            return this;
        }

        public SimpleOrder build() {
            SimpleOrder simpleOrder = new SimpleOrder();
            simpleOrder.setId(id);
            simpleOrder.setSenderAddress(senderAddress);
            simpleOrder.setSenderPhone(senderPhone);
            simpleOrder.setSenderName(senderName);
            simpleOrder.setOrderCode(orderCode);
            simpleOrder.setImage(image);
            return simpleOrder;
        }
    }
}
