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
    public static final float DEFAULT_VERIFY_SCORE = 0.76f;
    public static final float DEFAULT_MASK_VERIFY_SCORE = 0.72f;
    public static final int DEFAULT_QUALITY_SCORE = 25;
    public static final int DEFAULT_REGISTER_QUALITY_SCORE = 70;
    public static final int DEFAULT_DEVICE_ID = 0;
    public static final int DEFAULT_MASK_SCORE = 40;
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

    public static String fingerPositionCovert(byte finger) {
        switch ((int) finger) {
            case 11:
                return "右手拇指";
            case 12:
                return "右手食指";
            case 13:
                return "右手中指";
            case 14:
                return "右手环指";
            case 15:
                return "右手小指";
            case 16:
                return "左手拇指";
            case 17:
                return "左手食指";
            case 18:
                return "左手中指";
            case 19:
                return "左手环指";
            case 20:
                return "左手小指";
            case 97:
                return "右手不确定指位";
            case 98:
                return "左手不确定指位";
            case 99:
                return "其他不确定指位";
            default:
                return "其他不确定指位";
        }
    }

}
