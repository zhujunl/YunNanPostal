package com.miaxis.postal.util;

import com.google.gson.Gson;

public class ValueUtil {

    public final static Gson GSON = new Gson();

    public static final String DEFAULT_BASE_HOST = "http://192.168.5.110:8088/policebus/";
    public static final float DEFAULT_VERIFY_SCORE = 0.70f;
    public static final int DEFAULT_QUALITY_SCORE = 50;

    public static final String SUCCESS = "200";

}
