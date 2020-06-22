package com.miaxis.postal.data.entity;

public class Draft {

    private String name;
    private String cardNumber;
    private int orderCount;

    public Draft() {
    }

    private Draft(Builder builder) {
        setName(builder.name);
        setCardNumber(builder.cardNumber);
        setOrderCount(builder.orderCount);
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

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public static final class Builder {
        private String name;
        private String cardNumber;
        private int orderCount;

        public Builder() {
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder cardNumber(String val) {
            cardNumber = val;
            return this;
        }

        public Builder orderCount(int val) {
            orderCount = val;
            return this;
        }

        public Draft build() {
            return new Draft(this);
        }
    }
}
