package com.miaxis.postal.data.entity;

import android.graphics.Bitmap;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class IDCardRecord {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    /* 注释说明：二代证 / 港澳台 / 外国人永久居留证 */
    /**
     * 卡片类型 空值=二代证，J=港澳台，I=外国人永久居留证
     **/
    private String cardType;
    /**
     * 物理编号
     **/
    private String cardId;
    /**
     * 姓名
     **/
    private String name;
    /**
     * 出生日期
     **/
    private String birthday;
    /**
     * 地址 / 地址 / 空值
     **/
    private String address;
    /**
     * 身份证号码 / 身份证号 / 永久居留证号码
     **/
    private String cardNumber;
    /**
     * 签发机构 / 签发机构 / 申请受理机关代码
     **/
    private String issuingAuthority;
    /**
     * 有效期开始
     **/
    private String validateStart;
    /**
     * 有效期结束
     **/
    private String validateEnd;
    /**
     * 男/女
     **/
    private String sex;
    /**
     * 民族 / 空值 / 国籍或所在地区代码
     **/
    private String nation;


    /**
     * 港澳台：通行证号码
     **/
    private String passNumber;
    /**
     * 港澳台：签发次数
     **/
    private String issueCount;
    /**
     * 外国人：中文姓名
     **/
    private String chineseName;
    /**
     * 外国人：证件版本号
     **/
    private String version;

    /**
     * 身份证照片
     **/
    private String cardPicture;
    /**
     * 现场人员照片
     **/
    private String facePicture;

    /**
     * 人证核验缓存
     **/
    private String verifyId;

    private Date verifyTime;

    private boolean upload;

    private String verifyType; //1:人脸，2:指纹

    private int chekStatus; //核验状态 0：未核验 1：核验通过  2：核验未通过

    private String personId;

    private String checkId;

    private String manualType;

    private boolean draft;

    private String senderPhone;

    private String senderAddress;

    private String webCardPath;

    private  String webFacePath;

    private int type=1;



    /**
     * 身份证照片Bitmap
     **/
    @Ignore
    private Bitmap cardBitmap;
    /**
     * 人脸比对通过照片Bitmap
     **/
    @Ignore
    private Bitmap faceBitmap;
    /**
     * 指纹0
     **/
    @Ignore
    private String fingerprint0;
    /**
     * 指纹0指位
     **/
    @Ignore
    private String fingerprintPosition0;
    /**
     * 指纹1
     **/
    @Ignore
    private String fingerprint1;
    /**
     * 指纹1指位
     **/
    @Ignore
    private String fingerprintPosition1;

    public IDCardRecord() {
    }

    private IDCardRecord(Builder builder) {
        setId(builder.id);
        setCardType(builder.cardType);
        setCardId(builder.cardId);
        setName(builder.name);
        setBirthday(builder.birthday);
        setAddress(builder.address);
        setCardNumber(builder.cardNumber);
        setIssuingAuthority(builder.issuingAuthority);
        setValidateStart(builder.validateStart);
        setValidateEnd(builder.validateEnd);
        setSex(builder.sex);
        setChekStatus(builder.chekStatus);
        setNation(builder.nation);
        setPassNumber(builder.passNumber);
        setIssueCount(builder.issueCount);
        setChineseName(builder.chineseName);
        setVersion(builder.version);
        setCardPicture(builder.cardPicture);
        setFacePicture(builder.facePicture);
        setVerifyId(builder.verifyId);
        setVerifyTime(builder.verifyTime);
        setUpload(builder.upload);
        setVerifyType(builder.verifyType);
        setPersonId(builder.personId);
        setCheckId(builder.checkId);
        setManualType(builder.manualType);
        setDraft(builder.draft);
        setSenderPhone(builder.senderPhone);
        setSenderAddress(builder.senderAddress);
        setCardBitmap(builder.cardBitmap);
        setFaceBitmap(builder.faceBitmap);
        setFingerprint0(builder.fingerprint0);
        setFingerprintPosition0(builder.fingerprintPosition0);
        setFingerprint1(builder.fingerprint1);
        setFingerprintPosition1(builder.fingerprintPosition1);
        setWebCardPath(builder.webCardPath);
        setWebFacePath(builder.webFacePath);
        setType(builder.type);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public String getValidateStart() {
        return validateStart;
    }

    public void setValidateStart(String validateStart) {
        this.validateStart = validateStart;
    }

    public String getValidateEnd() {
        return validateEnd;
    }

    public void setValidateEnd(String validateEnd) {
        this.validateEnd = validateEnd;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getFingerprint0() {
        return fingerprint0;
    }

    public void setFingerprint0(String fingerprint0) {
        this.fingerprint0 = fingerprint0;
    }

    public String getFingerprintPosition0() {
        return fingerprintPosition0;
    }

    public void setFingerprintPosition0(String fingerprintPosition0) {
        this.fingerprintPosition0 = fingerprintPosition0;
    }

    public String getFingerprint1() {
        return fingerprint1;
    }

    public void setFingerprint1(String fingerprint1) {
        this.fingerprint1 = fingerprint1;
    }

    public String getFingerprintPosition1() {
        return fingerprintPosition1;
    }

    public void setFingerprintPosition1(String fingerprintPosition1) {
        this.fingerprintPosition1 = fingerprintPosition1;
    }

    public String getPassNumber() {
        return passNumber;
    }

    public void setPassNumber(String passNumber) {
        this.passNumber = passNumber;
    }

    public String getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(String issueCount) {
        this.issueCount = issueCount;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCardPicture() {
        return cardPicture;
    }

    public void setCardPicture(String cardPicture) {
        this.cardPicture = cardPicture;
    }

    public String getFacePicture() {
        return facePicture;
    }

    public void setFacePicture(String facePicture) {
        this.facePicture = facePicture;
    }

    public String getVerifyId() {
        return verifyId;
    }

    public void setVerifyId(String verifyId) {
        this.verifyId = verifyId;
    }

    public Date getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(Date verifyTime) {
        this.verifyTime = verifyTime;
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public String getVerifyType() {
        return verifyType;
    }

    public void setVerifyType(String verifyType) {
        this.verifyType = verifyType;
    }

    public Bitmap getCardBitmap() {
        return cardBitmap;
    }

    public void setCardBitmap(Bitmap cardBitmap) {
        this.cardBitmap = cardBitmap;
    }

    public Bitmap getFaceBitmap() {
        return faceBitmap;
    }

    public void setFaceBitmap(Bitmap faceBitmap) {
        this.faceBitmap = faceBitmap;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getCheckId() {
        return checkId;
    }

    public void setCheckId(String checkId) {
        this.checkId = checkId;
    }

    public String getManualType() {
        return manualType;
    }

    public void setManualType(String manualType) {
        this.manualType = manualType;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public int getChekStatus() {
        return chekStatus;
    }

    public void setChekStatus(int chekStatus) {
        this.chekStatus = chekStatus;
    }

    public String getWebCardPath() {
        return webCardPath;
    }

    public void setWebCardPath(String webCardPath) {
        this.webCardPath = webCardPath;
    }

    public String getWebFacePath() {
        return webFacePath;
    }

    public void setWebFacePath(String webFacePath) {
        this.webFacePath = webFacePath;
    }

    @Override
    public String toString() {
        return "IDCardRecord{" +
                "id=" + id +
                ", cardType='" + cardType + '\'' +
                ", cardId='" + cardId + '\'' +
                ", name='" + name + '\'' +
                ", birthday='" + birthday + '\'' +
                ", address='" + address + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", issuingAuthority='" + issuingAuthority + '\'' +
                ", validateStart='" + validateStart + '\'' +
                ", validateEnd='" + validateEnd + '\'' +
                ", sex='" + sex + '\'' +
                ", nation='" + nation + '\'' +
                ", passNumber='" + passNumber + '\'' +
                ", issueCount='" + issueCount + '\'' +
                ", chineseName='" + chineseName + '\'' +
                ", version='" + version + '\'' +
                ", cardPicture='" + cardPicture + '\'' +
                ", facePicture='" + facePicture + '\'' +
                ", verifyId='" + verifyId + '\'' +
                ", verifyTime=" + verifyTime +
                ", upload=" + upload +
                ", verifyType='" + verifyType + '\'' +
                ", chekStatus=" + chekStatus +
                ", personId='" + personId + '\'' +
                ", checkId='" + checkId + '\'' +
                ", manualType='" + manualType + '\'' +
                ", draft=" + draft +
                ", senderPhone='" + senderPhone + '\'' +
                ", senderAddress='" + senderAddress + '\'' +
                ", webCardPath='" + webCardPath + '\'' +
                ", webFacePath='" + webFacePath + '\'' +
                ", type=" + type +
                ", cardBitmap=" + cardBitmap +
                ", faceBitmap=" + faceBitmap +
                ", fingerprint0='" + fingerprint0 + '\'' +
                ", fingerprintPosition0='" + fingerprintPosition0 + '\'' +
                ", fingerprint1='" + fingerprint1 + '\'' +
                ", fingerprintPosition1='" + fingerprintPosition1 + '\'' +
                '}';
    }

    public static final class Builder {
        private Long id;
        private String cardType;
        private String cardId;
        private String name;
        private String birthday;
        private String address;
        private String cardNumber;
        private String issuingAuthority;
        private String validateStart;
        private String validateEnd;
        private String sex;
        private String nation;
        private String passNumber;
        private String issueCount;
        private String chineseName;
        private String version;
        private String cardPicture;
        private String facePicture;
        private String verifyId;
        private Date verifyTime;
        private boolean upload;
        private String verifyType;
        private String personId;
        private String checkId;
        private String manualType;
        private boolean draft;
        private String senderPhone;
        private String senderAddress;
        private Bitmap cardBitmap;
        private Bitmap faceBitmap;
        private String fingerprint0;
        private String fingerprintPosition0;
        private String fingerprint1;
        private String fingerprintPosition1;
        private int chekStatus; // 核验状态 0：未核验 1：核验通过  2：核验未通过
        private String webCardPath;
        private String webFacePath;
        private  int type;

        public Builder() {
        }

        public Builder chekStatus(int chekStatus) {
            this.chekStatus = chekStatus;
            return this;
        }

        public Builder setWebCardPath(String webCardPath) {
            this.webCardPath = webCardPath;
            return this;
        }

        public Builder setWebFacePath(String webFacePath) {
            this.webFacePath = webFacePath;
            return this;
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder cardType(String val) {
            cardType = val;
            return this;
        }

        public Builder cardId(String val) {
            cardId = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder birthday(String val) {
            birthday = val;
            return this;
        }

        public Builder address(String val) {
            address = val;
            return this;
        }

        public Builder cardNumber(String val) {
            cardNumber = val;
            return this;
        }

        public Builder issuingAuthority(String val) {
            issuingAuthority = val;
            return this;
        }

        public Builder validateStart(String val) {
            validateStart = val;
            return this;
        }

        public Builder validateEnd(String val) {
            validateEnd = val;
            return this;
        }

        public Builder sex(String val) {
            sex = val;
            return this;
        }

        public Builder nation(String val) {
            nation = val;
            return this;
        }

        public Builder passNumber(String val) {
            passNumber = val;
            return this;
        }

        public Builder issueCount(String val) {
            issueCount = val;
            return this;
        }

        public Builder chineseName(String val) {
            chineseName = val;
            return this;
        }

        public Builder version(String val) {
            version = val;
            return this;
        }

        public Builder cardPicture(String val) {
            cardPicture = val;
            return this;
        }

        public Builder facePicture(String val) {
            facePicture = val;
            return this;
        }

        public Builder verifyId(String val) {
            verifyId = val;
            return this;
        }

        public Builder verifyTime(Date val) {
            verifyTime = val;
            return this;
        }

        public Builder upload(boolean val) {
            upload = val;
            return this;
        }

        public Builder verifyType(String val) {
            verifyType = val;
            return this;
        }

        public Builder personId(String val) {
            personId = val;
            return this;
        }

        public Builder checkId(String val) {
            checkId = val;
            return this;
        }

        public Builder manualType(String val) {
            manualType = val;
            return this;
        }

        public Builder draft(boolean val) {
            draft = val;
            return this;
        }

        public Builder senderPhone(String val) {
            senderPhone = val;
            return this;
        }

        public Builder senderAddress(String val) {
            senderAddress = val;
            return this;
        }

        public Builder cardBitmap(Bitmap val) {
            cardBitmap = val;
            return this;
        }

        public Builder faceBitmap(Bitmap val) {
            faceBitmap = val;
            return this;
        }

        public Builder fingerprint0(String val) {
            fingerprint0 = val;
            return this;
        }

        public Builder fingerprintPosition0(String val) {
            fingerprintPosition0 = val;
            return this;
        }

        public Builder fingerprint1(String val) {
            fingerprint1 = val;
            return this;
        }

        public Builder fingerprintPosition1(String val) {
            fingerprintPosition1 = val;
            return this;
        }

        public Builder setType(int type) {
            this.type = type;
            return this;
        }

        public IDCardRecord build() {
            return new IDCardRecord(this);
        }
    }
}
