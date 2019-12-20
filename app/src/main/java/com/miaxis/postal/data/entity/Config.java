package com.miaxis.postal.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Config {

    @PrimaryKey
    private Long id;
    private String host;
    private String mac;
    private int qualityScore;
    private float verifyScore;

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

    public int getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(int qualityScore) {
        this.qualityScore = qualityScore;
    }

    public float getVerifyScore() {
        return verifyScore;
    }

    public void setVerifyScore(float verifyScore) {
        this.verifyScore = verifyScore;
    }

    public static final class ConfigBuilder {
        private Long id;
        private String host;
        private String mac;
        private int qualityScore;
        private float verifyScore;

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

        public ConfigBuilder qualityScore(int qualityScore) {
            this.qualityScore = qualityScore;
            return this;
        }

        public ConfigBuilder verifyScore(float verifyScore) {
            this.verifyScore = verifyScore;
            return this;
        }

        public Config build() {
            Config config = new Config();
            config.setId(id);
            config.setHost(host);
            config.setMac(mac);
            config.setQualityScore(qualityScore);
            config.setVerifyScore(verifyScore);
            return config;
        }
    }
}
