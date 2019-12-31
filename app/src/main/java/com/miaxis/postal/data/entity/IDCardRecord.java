package com.miaxis.postal.data.entity;

import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class IDCardRecord {

    @PrimaryKey(autoGenerate = true)
    private long id;

    /* 注释说明：二代证 / 港澳台 / 外国人永久居留证 */
    /** 卡片类型 空值=二代证，J=港澳台，I=外国人永久居留证 **/
    private String cardType;
    /** 物理编号 **/
    private String cardId;
    /** 姓名 **/
    private String name;
    /** 出生日期 **/
    private String birthday;
    /** 地址 / 地址 / 空值 **/
    private String address;
    /** 身份证号码 / 身份证号 / 永久居留证号码 **/
    private String cardNumber;
    /** 签发机构 / 签发机构 / 申请受理机关代码 **/
    private String issuingAuthority;
    /** 有效期开始 **/
    private String validateStart;
    /** 有效期结束 **/
    private String validateEnd;
    /** 男/女 **/
    private String sex;
    /** 民族 / 空值 / 国籍或所在地区代码 **/
    private String nation;
    /** 指纹0 **/
    private String fingerprint0;
    /** 指纹0指位 **/
    private String fingerprintPosition0;
    /** 指纹1 **/
    private String fingerprint1;
    /** 指纹1指位 **/
    private String fingerprintPosition1;

    /** 港澳台：通行证号码 **/
    private String passNumber;
    /** 港澳台：签发次数 **/
    private String issueCount;
    /** 外国人：中文姓名 **/
    private String chineseName;
    /** 外国人：证件版本号 **/
    private String version;

    /** 身份证照片 **/
    private String cardPicture;
    /** 现场人员照片 **/
    private String facePicture;

    /** 人证核验缓存编号 **/
    private String verifyId;

    /** 身份证照片Bitmap **/
    @Ignore
    private Bitmap cardBitmap;
    /** 人脸比对通过照片Bitmap **/
    @Ignore
    private Bitmap faceBitmap;

    public IDCardRecord() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public static final class IDCardRecordBuilder {
        private long id;
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
        private String fingerprint0;
        private String fingerprintPosition0;
        private String fingerprint1;
        private String fingerprintPosition1;
        private String passNumber;
        private String issueCount;
        private String chineseName;
        private String version;
        private String cardPicture;
        private String facePicture;
        private String verifyId;
        private Bitmap cardBitmap;
        private Bitmap faceBitmap;

        private IDCardRecordBuilder() {
        }

        public static IDCardRecordBuilder anIDCardRecord() {
            return new IDCardRecordBuilder();
        }

        public IDCardRecordBuilder id(long id) {
            this.id = id;
            return this;
        }

        public IDCardRecordBuilder cardType(String cardType) {
            this.cardType = cardType;
            return this;
        }

        public IDCardRecordBuilder cardId(String cardId) {
            this.cardId = cardId;
            return this;
        }

        public IDCardRecordBuilder name(String name) {
            this.name = name;
            return this;
        }

        public IDCardRecordBuilder birthday(String birthday) {
            this.birthday = birthday;
            return this;
        }

        public IDCardRecordBuilder address(String address) {
            this.address = address;
            return this;
        }

        public IDCardRecordBuilder cardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public IDCardRecordBuilder issuingAuthority(String issuingAuthority) {
            this.issuingAuthority = issuingAuthority;
            return this;
        }

        public IDCardRecordBuilder validateStart(String validateStart) {
            this.validateStart = validateStart;
            return this;
        }

        public IDCardRecordBuilder validateEnd(String validateEnd) {
            this.validateEnd = validateEnd;
            return this;
        }

        public IDCardRecordBuilder sex(String sex) {
            this.sex = sex;
            return this;
        }

        public IDCardRecordBuilder nation(String nation) {
            this.nation = nation;
            return this;
        }

        public IDCardRecordBuilder fingerprint0(String fingerprint0) {
            this.fingerprint0 = fingerprint0;
            return this;
        }

        public IDCardRecordBuilder fingerprintPosition0(String fingerprintPosition0) {
            this.fingerprintPosition0 = fingerprintPosition0;
            return this;
        }

        public IDCardRecordBuilder fingerprint1(String fingerprint1) {
            this.fingerprint1 = fingerprint1;
            return this;
        }

        public IDCardRecordBuilder fingerprintPosition1(String fingerprintPosition1) {
            this.fingerprintPosition1 = fingerprintPosition1;
            return this;
        }

        public IDCardRecordBuilder passNumber(String passNumber) {
            this.passNumber = passNumber;
            return this;
        }

        public IDCardRecordBuilder issueCount(String issueCount) {
            this.issueCount = issueCount;
            return this;
        }

        public IDCardRecordBuilder chineseName(String chineseName) {
            this.chineseName = chineseName;
            return this;
        }

        public IDCardRecordBuilder version(String version) {
            this.version = version;
            return this;
        }

        public IDCardRecordBuilder cardPicture(String cardPicture) {
            this.cardPicture = cardPicture;
            return this;
        }

        public IDCardRecordBuilder facePicture(String facePicture) {
            this.facePicture = facePicture;
            return this;
        }

        public IDCardRecordBuilder verifyId(String verifyId) {
            this.verifyId = verifyId;
            return this;
        }

        public IDCardRecordBuilder cardBitmap(Bitmap cardBitmap) {
            this.cardBitmap = cardBitmap;
            return this;
        }

        public IDCardRecordBuilder faceBitmap(Bitmap faceBitmap) {
            this.faceBitmap = faceBitmap;
            return this;
        }

        public IDCardRecord build() {
            IDCardRecord iDCardRecord = new IDCardRecord();
            iDCardRecord.setId(id);
            iDCardRecord.setCardType(cardType);
            iDCardRecord.setCardId(cardId);
            iDCardRecord.setName(name);
            iDCardRecord.setBirthday(birthday);
            iDCardRecord.setAddress(address);
            iDCardRecord.setCardNumber(cardNumber);
            iDCardRecord.setIssuingAuthority(issuingAuthority);
            iDCardRecord.setValidateStart(validateStart);
            iDCardRecord.setValidateEnd(validateEnd);
            iDCardRecord.setSex(sex);
            iDCardRecord.setNation(nation);
            iDCardRecord.setFingerprint0(fingerprint0);
            iDCardRecord.setFingerprintPosition0(fingerprintPosition0);
            iDCardRecord.setFingerprint1(fingerprint1);
            iDCardRecord.setFingerprintPosition1(fingerprintPosition1);
            iDCardRecord.setPassNumber(passNumber);
            iDCardRecord.setIssueCount(issueCount);
            iDCardRecord.setChineseName(chineseName);
            iDCardRecord.setVersion(version);
            iDCardRecord.setCardPicture(cardPicture);
            iDCardRecord.setFacePicture(facePicture);
            iDCardRecord.setVerifyId(verifyId);
            iDCardRecord.setCardBitmap(cardBitmap);
            iDCardRecord.setFaceBitmap(faceBitmap);
            return iDCardRecord;
        }
    }
}
