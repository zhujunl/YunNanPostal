package com.miaxis.postal.data.dto;

import com.miaxis.postal.data.entity.Update;
import com.miaxis.postal.data.exception.MyException;

public class UpdateDto implements Mapper<Update> {

    private String version;
    private String url;
    private String releaseTime;
    private boolean forced;

    public UpdateDto() {
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

    @Override
    public Update transform() throws MyException {
        try {
            return new Update.Builder()
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException("应用更新信息解码失败，原因：" + e.getMessage());
        }
    }
}
