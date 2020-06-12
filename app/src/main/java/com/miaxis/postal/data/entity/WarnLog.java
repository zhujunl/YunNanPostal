package com.miaxis.postal.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class WarnLog {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String verifyId;
    private String sendAddress;
    private String sendCardNo;
    private String sendPhone;
    private String sendName;
    private long expressmanId;
    private String expressmanName;
    private String expressmanPhone;
    private String createTime;
    private boolean upload;

    private Integer warnId;

    public WarnLog() {
    }

    private WarnLog(Builder builder) {
        setId(builder.id);
        setVerifyId(builder.verifyId);
        setSendAddress(builder.sendAddress);
        setSendCardNo(builder.sendCardNo);
        setSendPhone(builder.sendPhone);
        setSendName(builder.sendName);
        setExpressmanId(builder.expressmanId);
        setExpressmanName(builder.expressmanName);
        setExpressmanPhone(builder.expressmanPhone);
        setCreateTime(builder.createTime);
        setUpload(builder.upload);
        setWarnId(builder.warnId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVerifyId() {
        return verifyId;
    }

    public void setVerifyId(String verifyId) {
        this.verifyId = verifyId;
    }

    public String getSendAddress() {
        return sendAddress;
    }

    public void setSendAddress(String sendAddress) {
        this.sendAddress = sendAddress;
    }

    public String getSendCardNo() {
        return sendCardNo;
    }

    public void setSendCardNo(String sendCardNo) {
        this.sendCardNo = sendCardNo;
    }

    public String getSendPhone() {
        return sendPhone;
    }

    public void setSendPhone(String sendPhone) {
        this.sendPhone = sendPhone;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public long getExpressmanId() {
        return expressmanId;
    }

    public void setExpressmanId(long expressmanId) {
        this.expressmanId = expressmanId;
    }

    public String getExpressmanName() {
        return expressmanName;
    }

    public void setExpressmanName(String expressmanName) {
        this.expressmanName = expressmanName;
    }

    public String getExpressmanPhone() {
        return expressmanPhone;
    }

    public void setExpressmanPhone(String expressmanPhone) {
        this.expressmanPhone = expressmanPhone;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public Integer getWarnId() {
        return warnId;
    }

    public void setWarnId(Integer warnId) {
        this.warnId = warnId;
    }

    public static final class Builder {
        private int id;
        private String verifyId;
        private String sendAddress;
        private String sendCardNo;
        private String sendPhone;
        private String sendName;
        private long expressmanId;
        private String expressmanName;
        private String expressmanPhone;
        private String createTime;
        private boolean upload;
        private Integer warnId;

        public Builder() {
        }

        public Builder id(int val) {
            id = val;
            return this;
        }

        public Builder verifyId(String val) {
            verifyId = val;
            return this;
        }

        public Builder sendAddress(String val) {
            sendAddress = val;
            return this;
        }

        public Builder sendCardNo(String val) {
            sendCardNo = val;
            return this;
        }

        public Builder sendPhone(String val) {
            sendPhone = val;
            return this;
        }

        public Builder sendName(String val) {
            sendName = val;
            return this;
        }

        public Builder expressmanId(long val) {
            expressmanId = val;
            return this;
        }

        public Builder expressmanName(String val) {
            expressmanName = val;
            return this;
        }

        public Builder expressmanPhone(String val) {
            expressmanPhone = val;
            return this;
        }

        public Builder createTime(String val) {
            createTime = val;
            return this;
        }

        public Builder upload(boolean val) {
            upload = val;
            return this;
        }

        public Builder warnId(Integer val) {
            warnId = val;
            return this;
        }

        public WarnLog build() {
            return new WarnLog(this);
        }
    }
}
