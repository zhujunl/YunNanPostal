package com.miaxis.postal.util;

import com.google.gson.Gson;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;

public class ValueUtil {

    public final static Gson GSON = new Gson();

    public static final String DEFAULT_BASE_HOST = "http://192.168.5.110:8088/policebus/";
    public static final float DEFAULT_VERIFY_SCORE = 0.70f;
    public static final int DEFAULT_QUALITY_SCORE = 50;
    public static final int DEFAULT_REGISTER_QUALITY_SCORE = 80;
    public static final int DEFAULT_DEVICE_ID = 0;
    public static final int DEFAULT_HEART_BEAT_INTERVAL = 10 * 60;

    public static final String SUCCESS = "200";
    public static final String DEVICE_ENABLE = "1";
    public static final String DEVICE_UNABLE = "0";

    public static boolean isNetException(Throwable throwable) {
        if (throwable instanceof SocketTimeoutException
                || throwable instanceof ConnectException
                || throwable instanceof HttpException
                || throwable instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            return true;
        }
        return false;
    }

}
