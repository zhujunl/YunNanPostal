package com.miaxis.postal.data.entity;


import com.miaxis.postal.view.presenter.DownloadPresenter;

import java.io.File;

public class AppItem {

    public String Position;

    public String AppName;
    public String AppVersion;

    public String AppUrl;
    public String AppLocalPath;

    private AppItem() {
    }

    public AppItem(String appName, String appVersion, String appUrl) {
        this.AppName = appName;
        this.AppVersion = appVersion;
        this.AppUrl = appUrl;
//        this.AppLocalPath = DownloadPresenter.AppPath(this);
    }

    public boolean isDownload() {
        return new File(this.AppLocalPath).exists();
    }

    @Override
    public String toString() {
        return "AppItem{" +
                "Position='" + Position + '\'' +
                ", AppName='" + AppName + '\'' +
                ", AppVersion='" + AppVersion + '\'' +
                ", AppUrl='" + AppUrl + '\'' +
                ", AppLocalPath='" + AppLocalPath + '\'' +
                '}';
    }

}
