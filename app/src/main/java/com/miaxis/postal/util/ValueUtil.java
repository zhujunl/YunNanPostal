package com.miaxis.postal.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.gson.Gson;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;

public class ValueUtil {

    public final static Gson GSON = new Gson();

    public static final String DEFAULT_BASE_HOST = "http://183.129.171.153:9194/policebus/";
    public static final float DEFAULT_VERIFY_SCORE = 0.70f;
    public static final int DEFAULT_QUALITY_SCORE = 60;
    public static final int DEFAULT_REGISTER_QUALITY_SCORE = 80;
    public static final int DEFAULT_DEVICE_ID = 0;
    public static final int DEFAULT_HEART_BEAT_INTERVAL = 10 * 60;
    public static final int LOGIN_MODE_FINGER = 1;//1:仅指纹，2:仅人脸
    public static final int LOGIN_MODE_FACE = 2;//1:仅指纹，2:仅人脸
    public static final int DEFAULT_LOGIN_MODE = LOGIN_MODE_FINGER;

    public static final String SUCCESS = "200";
    public static final String DEVICE_ENABLE = "1";
    public static final String DEVICE_UNABLE = "0";
    public static final int PAGE_SIZE = 8;

    public static boolean isNetException(Throwable throwable) {
        if (throwable instanceof SocketTimeoutException
                || throwable instanceof ConnectException
                || throwable instanceof HttpException
                || throwable instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            return true;
        }
        return false;
    }

    public static String getCurVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
