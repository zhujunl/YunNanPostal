package com.miaxis.postal.data.entity;

public class Update {

    private String version;
    private String url;
    private String releaseTime;
    private boolean forced;

    public Update() {
    }

    private Update(Builder builder) {
        setVersion(builder.version);
        setUrl(builder.url);
        setReleaseTime(builder.releaseTime);
        setForced(builder.forced);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }

    public static final class Builder {
        private String version;
        private String url;
        private String releaseTime;
        private boolean forced;

        public Builder() {
        }

        public Builder version(String val) {
            version = val;
            return this;
        }

        public Builder url(String val) {
            url = val;
            return this;
        }

        public Builder releaseTime(String val) {
            releaseTime = val;
            return this;
        }

        public Builder forced(boolean val) {
            forced = val;
            return this;
        }

        public Update build() {
            return new Update(this);
        }
    }
}
