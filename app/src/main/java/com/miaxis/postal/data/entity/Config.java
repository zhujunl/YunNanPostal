package com.miaxis.postal.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Config {

    @PrimaryKey
    private Long id;
    private String host;
    private String deviceIMEI;
    private int qualityScore;
    private int registerQualityScore;
    private float verifyScore;
    private float verifyMaskScore;
    private int maskScore;
    private String deviceStatus;
    private int heartBeatInterval;

    public Config() {
    }

    private Config(Builder builder) {
        setId(builder.id);
        setHost(builder.host);
        setDeviceIMEI(builder.deviceIMEI);
        setQualityScore(builder.qualityScore);
        setRegisterQualityScore(builder.registerQualityScore);
        setVerifyScore(builder.verifyScore);
        setVerifyMaskScore(builder.verifyMaskScore);
        setMaskScore(builder.maskScore);
        setDeviceStatus(builder.deviceStatus);
        setHeartBeatInterval(builder.heartBeatInterval);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDeviceIMEI() {
        return deviceIMEI;
    }

    public void setDeviceIMEI(String deviceIMEI) {
        this.deviceIMEI = deviceIMEI;
    }

    public int getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(int qualityScore) {
        this.qualityScore = qualityScore;
    }

    public int getRegisterQualityScore() {
        return registerQualityScore;
    }

    public void setRegisterQualityScore(int registerQualityScore) {
        this.registerQualityScore = registerQualityScore;
    }

    public float getVerifyScore() {
        return verifyScore;
    }

    public void setVerifyScore(float verifyScore) {
        this.verifyScore = verifyScore;
    }

    public float getVerifyMaskScore() {
        return verifyMaskScore;
    }

    public void setVerifyMaskScore(float verifyMaskScore) {
        this.verifyMaskScore = verifyMaskScore;
    }

    public int getMaskScore() {
        return maskScore;
    }

    public void setMaskScore(int maskScore) {
        this.maskScore = maskScore;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public int getHeartBeatInterval() {
        return heartBeatInterval;
    }

    public void setHeartBeatInterval(int heartBeatInterval) {
        this.heartBeatInterval = heartBeatInterval;
    }

    public static final class Builder {
        private Long id;
        private String host;
        private String deviceIMEI;
        private int qualityScore;
        private int registerQualityScore;
        private float verifyScore;
        private float verifyMaskScore;
        private int maskScore;
        private String deviceStatus;
        private int heartBeatInterval;

        public Builder() {
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder host(String val) {
            host = val;
            return this;
        }

        public Builder deviceIMEI(String val) {
            deviceIMEI = val;
            return this;
        }

        public Builder qualityScore(int val) {
            qualityScore = val;
            return this;
        }

        public Builder registerQualityScore(int val) {
            registerQualityScore = val;
            return this;
        }

        public Builder verifyScore(float val) {
            verifyScore = val;
            return this;
        }

        public Builder verifyMaskScore(float val) {
            verifyMaskScore = val;
            return this;
        }

        public Builder maskScore(int val) {
            maskScore = val;
            return this;
        }

        public Builder deviceStatus(String val) {
            deviceStatus = val;
            return this;
        }

        public Builder heartBeatInterval(int val) {
            heartBeatInterval = val;
            return this;
        }

        public Config build() {
            return new Config(this);
        }
    }
}
