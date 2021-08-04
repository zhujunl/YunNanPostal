package com.miaxis.postal.data.event;

import com.miaxis.postal.data.entity.Express;

public class ExpressEditEvent {

    public static final int MODE_MODIFY = 1;
    public static final int MODE_DELETE = 2;
    public static final int MODE_ALARM = 3;

    private int mode;
    private Express express;
    private String name, phone, goodName, goodCounts,sendAddress;//临时增加属性 用于记录客户信息

    public ExpressEditEvent(int mode, Express express) {
        this.mode = mode;
        this.express = express;
    }

    public ExpressEditEvent(int mode, Express express, String name, String phone, String goodName, String goodCounts,String sendAddress) {
        this.mode = mode;
        this.express = express;
        this.name = name;
        this.phone = phone;
        this.goodName = goodName;
        this.goodCounts = goodCounts;
        this.sendAddress = sendAddress;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public Express getExpress() {
        return express;
    }

    public void setExpress(Express express) {
        this.express = express;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getGoodCounts() {
        return goodCounts;
    }

    public void setGoodCounts(String goodCounts) {
        this.goodCounts = goodCounts;
    }

    public String getSendAddress() {
        return sendAddress;
    }

    public void setSendAddress(String sendAddress) {
        this.sendAddress = sendAddress;
    }

    @Override
    public String toString() {
        return "ExpressEditEvent{" +
                "mode=" + mode +
                ", express=" + express +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", goodName='" + goodName + '\'' +
                ", goodCounts='" + goodCounts + '\'' +
                ", sendAddress='" + sendAddress + '\'' +
                '}';
    }
}
