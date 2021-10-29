package com.miaxis.postal.data.entity;

import com.miaxis.postal.view.presenter.DownloadPresenter;

import java.io.File;
import java.util.List;

public class AppEntity {

    private String code;
    private String message;
    private List<DataBean>data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AppEntity{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    /**
     * Company:品牌
     * name:app名称
     * version:版本号
     * url:下载地址
     */
    public static class DataBean{
        public String orgName;
        public String name;
        public String version;
        public String url;
        public String AppLocalPath;
        public String position;

        public String getAppLocalPath() {
            return AppLocalPath;
        }

        public void setAppLocalPath(String appLocalPath) {
            AppLocalPath = appLocalPath;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return "V "+version;
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


        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public DataBean(String orgName, String name, String version, String url, String position) {
            this.orgName = orgName;
            this.name = name;
            this.version = version;
            this.position = position;
            this.url = DownloadPresenter.AppPath(this);
        }

        public boolean isDownload() {
            return new File(this.url).exists();
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "orgName='" + orgName + '\'' +
                    ", name='" + name + '\'' +
                    ", version='" + version + '\'' +
                    ", url='" + url + '\'' +
                    ", AppLocalPath='" + AppLocalPath + '\'' +
                    ", position='" + position + '\'' +
                    '}';
        }
    }

}
