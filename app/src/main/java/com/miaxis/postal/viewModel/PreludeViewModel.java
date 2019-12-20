package com.miaxis.postal.viewModel;

import android.Manifest;
import android.text.TextUtils;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import com.miaxis.postal.app.PostalApp;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.util.ValueUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PreludeViewModel extends BaseViewModel {

    public ObservableField<String> hint = new ObservableField<>("");
    public ObservableBoolean errorMode = new ObservableBoolean(Boolean.FALSE);

    private SingleLiveEvent<Boolean> initSuccess = new SingleLiveEvent<>();

    public PreludeViewModel() {
    }

    public LiveData<Boolean> getInitSuccess() {
        return initSuccess;
    }

    public void requirePermission(Fragment fragment) {
        errorMode.set(Boolean.FALSE);
        hint.set("请授予权限");
        Disposable subscribe = new RxPermissions(fragment)
                .request(Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnNext(aBoolean -> hint.set("正在初始化"))
                .observeOn(Schedulers.io())
                .subscribe(success -> {
                    if (success) {
                        PostalApp.getInstance().initApplication(onAppInitListener);
                    } else {
                        throw new Exception("拒绝权限");
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    errorMode.set(Boolean.TRUE);
                    hint.set("" + throwable.getMessage());
                });
    }

    private PostalApp.OnAppInitListener onAppInitListener = (result, message) -> {
        if (result) {
//            hint.set("初始化成功");
            initSuccess.postValue(Boolean.TRUE);
        } else {
            errorMode.set(Boolean.TRUE);
            hint.set(message);
        }
    };

}
