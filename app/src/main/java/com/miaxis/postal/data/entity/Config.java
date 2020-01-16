package com.miaxis.postal.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Config {

    @PrimaryKey
    private Long id;
    private String host;
    private String mac;
    private int loginMode;
    private int qualityScore;
    private int registerQualityScore;
    private float verifyScore;
    private int deviceId;
    private String deviceStatus;
    private int heartBeatInterval;

    public Config() {
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

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getLoginMode() {
        return loginMode;
    }

    public void setLoginMode(int loginMode) {
        this.loginMode = loginMode;
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

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
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

    public static final class ConfigBuilder {
        private Long id;
        private String host;
        private String mac;
        private int loginMode;
        private int qualityScore;
        private int registerQualityScore;
        private float verifyScore;
        private int deviceId;
        private String deviceStatus;
        private int heartBeatInterval;

        private ConfigBuilder() {
        }

        public static ConfigBuilder aConfig() {
            return new ConfigBuilder();
        }

        public ConfigBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ConfigBuilder host(String host) {
            this.host = host;
            return this;
        }

        public ConfigBuilder mac(String mac) {
            this.mac = mac;
            return this;
        }

        public ConfigBuilder loginMode(int loginMode) {
            this.loginMode = loginMode;
            return this;
        }

        public ConfigBuilder qualityScore(int qualityScore) {
            this.qualityScore = qualityScore;
            return this;
        }

        public ConfigBuilder registerQualityScore(int registerQualityScore) {
            this.registerQualityScore = registerQualityScore;
            return this;
        }

        public ConfigBuilder verifyScore(float verifyScore) {
            this.verifyScore = verifyScore;
            return this;
        }

        public ConfigBuilder deviceId(int deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public ConfigBuilder deviceStatus(String deviceStatus) {
            this.deviceStatus = deviceStatus;
            return this;
        }

        public ConfigBuilder heartBeatInterval(int heartBeatInterval) {
            this.heartBeatInterval = heartBeatInterval;
            return this;
        }

        public Config build() {
            Config config = new Config();
            config.setId(id);
            config.setHost(host);
            config.setMac(mac);
            config.setLoginMode(loginMode);
            config.setQualityScore(qualityScore);
            config.setRegisterQualityScore(registerQualityScore);
            config.setVerifyScore(verifyScore);
            config.setDeviceId(deviceId);
            config.setDeviceStatus(deviceStatus);
            config.setHeartBeatInterval(heartBeatInterval);
            return config;
        }
    }
}
