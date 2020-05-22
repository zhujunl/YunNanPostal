package com.miaxis.postal.data.dto;

import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.exception.MyException;

public class CourierDto implements Mapper<Courier> {

    private long id;
    private String name;
    private String cardNo;
    private String phone;
    private String photo;
    private String faceFeature;
    private String maskFaceFeature;
    private String finger1Feature;
    private String finger2Feature;
    private String createTime;
    private String password;

    public CourierDto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(String faceFeature) {
        this.faceFeature = faceFeature;
    }

    public String getMaskFaceFeature() {
        return maskFaceFeature;
    }

    public void setMaskFaceFeature(String maskFaceFeature) {
        this.maskFaceFeature = maskFaceFeature;
    }

    public String getFinger1Feature() {
        return finger1Feature;
    }

    public void setFinger1Feature(String finger1Feature) {
        this.finger1Feature = finger1Feature;
    }

    public String getFinger2Feature() {
        return finger2Feature;
    }

    public void setFinger2Feature(String finger2Feature) {
        this.finger2Feature = finger2Feature;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Courier transform() throws MyException {
        try {
            return new Courier.Builder()
                    .id(1L)
                    .courierId(id)
                    .name(name)
                    .cardNumber(cardNo)
                    .phone(phone)
                    .photo(photo)
                    .faceFeature(faceFeature)
                    .maskFaceFeature(maskFaceFeature)
                    .fingerFeature1(finger1Feature)
                    .fingerFeature2(finger2Feature)
                    .createTime(createTime)
                    .password(password)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException("快递员信息解码失败，原因：" + e.getMessage());
        }
    }
}
