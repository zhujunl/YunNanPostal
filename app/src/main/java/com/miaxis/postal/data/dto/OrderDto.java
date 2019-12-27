package com.miaxis.postal.data.dto;

import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.data.exception.MyException;

import java.util.List;

public class OrderDto implements Mapper<Order> {

    private int id;
    private int personId;
    private int checkId;
    private String sendAddress;
    private String sendPhone;
    private String sendName;
    private String orderCode;
    private String orderInfo;
    private String lat;
    private String lng;
    private String addresseeName;
    private String addresseeAddress;
    private String addresseePhone;
    private String pieceTime;
    private String checkImage;
    private String cardImage;
    private List<String> images;
    private String receipTime;
    private String createTime;

    public OrderDto() {
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

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getReceipTime() {
        return receipTime;
    }

    public void setReceipTime(String receipTime) {
        this.receipTime = receipTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public Order transform() throws MyException {
        try {
            return Order.OrderBuilder.anOrder()
                    .id(id)
                    .personId(personId)
                    .checkId(checkId)
                    .senderAddress(sendAddress)
                    .senderPhone(sendPhone)
                    .senderName(sendName)
                    .orderCode(orderCode)
                    .orderInfo(orderInfo)
                    .latitude(lat)
                    .longitude(lng)
                    .addresseeName(addresseeName)
                    .addresseeAddress(addresseeAddress)
                    .addresseePhone(addresseePhone)
                    .pieceTime(pieceTime)
                    .checkImage(checkImage)
                    .cardImage(cardImage)
                    .imageList(images)
                    .receiptTime(receipTime)
                    .createTime(createTime)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException("解析订单详细信息失败，原因：" + e.getMessage());
        }
    }
}
