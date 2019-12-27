package com.miaxis.postal.data.dto;

import com.miaxis.postal.data.entity.SimpleOrder;
import com.miaxis.postal.data.exception.MyException;

public class SimpleOrderDto implements Mapper<SimpleOrder> {

    private int id;
    private String sendAddress;
    private String sendPhone;
    private String sendName;
    private String orderCode;
    private String listImage;

    public SimpleOrderDto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSendAddress() {
        return sendAddress;
    }

    public void setSendAddress(String sendAddress) {
        this.sendAddress = sendAddress;
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

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getListImage() {
        return listImage;
    }

    public void setListImage(String listImage) {
        this.listImage = listImage;
    }

    @Override
    public SimpleOrder transform() throws MyException {
        try {
            return SimpleOrder.SimpleOrderBuilder.aSimpleOrder()
                    .id(id)
                    .senderAddress(sendAddress)
                    .senderPhone(sendPhone)
                    .senderName(sendName)
                    .orderCode(orderCode)
                    .image(listImage)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException("解析订单缩略信息出错，原因" + e.getMessage());
        }
    }
}
