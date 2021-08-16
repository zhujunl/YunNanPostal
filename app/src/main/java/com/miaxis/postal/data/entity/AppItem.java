package com.miaxis.postal.data.entity;


import android.text.TextUtils;

import java.io.File;

public class AppItem {

    public String Position;

    public String AppName;
    public String AppVersion;

    public String AppUrl;
    public String AppLocalPath;

    public AppItem() {
    }

    public boolean isDownload() {
        return !TextUtils.isEmpty(this.AppLocalPath) && new File(this.AppLocalPath).exists();
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
