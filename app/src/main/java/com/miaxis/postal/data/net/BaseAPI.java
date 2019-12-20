package com.miaxis.postal.data.net;

import android.text.TextUtils;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.util.ValueUtil;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BaseAPI {

    protected final static Retrofit.Builder RETROFIT = new Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

    protected static Retrofit getRetrofit() {
        return RETROFIT.baseUrl(ConfigManager.getInstance().getConfig().getHost()).build();
    }

    protected static Observable<PostalNet> getPostalNet() {
        return Observable.create(emitter -> {
            Retrofit retrofit = getRetrofit();
            PostalNet postalNet = retrofit.create(PostalNet.class);
            emitter.onNext(postalNet);
        });
    }

    protected static PostalNet getPostalNetSync() {
        return getRetrofit().create(PostalNet.class);
    }

    protected static Observable<ResponseEntity> handleResponse(Observable<ResponseEntity> observable) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(responseEntity -> {
                    if (!TextUtils.equals(responseEntity.getCode(), ValueUtil.SUCCESS)) {
                        throw new MyException(responseEntity.getMessage());
                    }
                });
    }

    protected static <T> Observable<ResponseEntity<T>> handleGenericityResponse(Observable<ResponseEntity<T>> observable) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(responseEntity -> {
                    if (!TextUtils.equals(responseEntity.getCode(), ValueUtil.SUCCESS)) {
                        throw new MyException(responseEntity.getMessage());
                    }
                });
    }

    protected static <T> Observable<ResponseEntity<T>> handleLocalResponse(Observable<ResponseEntity<T>> observable) {
        return observable.doOnNext(responseEntity -> {
            if (!TextUtils.equals(responseEntity.getCode(), ValueUtil.SUCCESS)) {
                throw new MyException(responseEntity.getMessage());
            }
        });
    }

}
