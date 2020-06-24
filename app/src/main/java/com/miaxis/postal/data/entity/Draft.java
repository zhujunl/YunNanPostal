package com.miaxis.postal.data.entity;

public class Draft {

    private Long idCardRecordId;
    private String name;
    private String cardNumber;
    private String verifyTime;
    private int orderCount;

    public Draft() {
    }

    private Draft(Builder builder) {
        setIdCardRecordId(builder.idCardRecordId);
        setName(builder.name);
        setCardNumber(builder.cardNumber);
        setVerifyTime(builder.verifyTime);
        setOrderCount(builder.orderCount);
    }

    public Long getIdCardRecordId() {
        return idCardRecordId;
    }

    public void setIdCardRecordId(Long idCardRecordId) {
        this.idCardRecordId = idCardRecordId;
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

    public String getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(String verifyTime) {
        this.verifyTime = verifyTime;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public static final class Builder {
        private Long idCardRecordId;
        private String name;
        private String cardNumber;
        private String verifyTime;
        private int orderCount;

        public Builder() {
        }

        public Builder idCardRecordId(Long val) {
            idCardRecordId = val;
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

        public Builder verifyTime(String val) {
            verifyTime = val;
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
