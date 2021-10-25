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


    /**
     * Company:品牌
     * name:app名称
     * version:版本号
     * url:下载地址
     */
    public static class DataBean{
        public String Company;
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

        public String getCompany() {
            return Company;
        }

        public void setCompany(String company) {
            Company = company;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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


        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public DataBean(String company, String name, String version, String url, String position) {
            Company = company;
            this.name = name;
            this.version = version;
            this.url = url;
            this.position = position;
            this.AppLocalPath = DownloadPresenter.AppPath(this);
        }

        public boolean isDownload() {
            return new File(this.AppLocalPath).exists();
        }
    }

}
