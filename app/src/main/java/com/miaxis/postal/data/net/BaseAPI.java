package com.miaxis.postal.data.net;

import android.text.TextUtils;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.util.ValueUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BaseAPI {

    private final static Retrofit.Builder RETROFIT_BUILDER = new Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

    private static Retrofit retrofit;

    private static Retrofit getRetrofit() {
        return retrofit;
    }

    public static void rebuildRetrofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS);

//        builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory());
//        builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier());
        OkHttpClient okHttpClient = builder.build();
        retrofit = RETROFIT_BUILDER
                .client(okHttpClient)
                .baseUrl(ConfigManager.getInstance().getConfig().getHost())
                .build();
    }

    protected static PostalNet getPostalNetSync() {
        return getRetrofit().create(PostalNet.class);
    }

}
