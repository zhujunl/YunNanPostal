package com.miaxis.postal.viewModel;

import android.Manifest;
import android.text.TextUtils;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.exception.NetResultFailedException;
import com.miaxis.postal.data.repository.DeviceRepository;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.ValueUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
                .observeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .subscribe(success -> {
                    if (success) {
                        App.getInstance().initApplication(onAppInitListener);
                    } else {
                        throw new Exception("拒绝权限");
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    errorMode.set(Boolean.TRUE);
                    hint.set("" + throwable.getMessage());
                });
    }

    private App.OnAppInitListener onAppInitListener = (result, message) -> {
        if (result) {
            Config config = ConfigManager.getInstance().getConfig();
            hint.set("初始化成功，正在查询设备状态");
            getDeviceStatus();
        } else {
            errorMode.set(Boolean.TRUE);
            hint.set(message);
        }
    };

    private void getDeviceStatus() {
        Disposable subscribe = Observable.create((ObservableOnSubscribe<String>) emitter -> {
            String deviceStatus = DeviceRepository.getInstance().getDeviceStatus();
            emitter.onNext(deviceStatus);
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .doOnNext(s -> {
                    Config config = ConfigManager.getInstance().getConfig();
                    config.setDeviceStatus(s);
                    ConfigManager.getInstance().saveConfigSync(config);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::initResult, throwable -> {
                    if (throwable instanceof NetResultFailedException) {
                        showErrorMessage(throwable.getMessage());
                    } else {
                        Config config = ConfigManager.getInstance().getConfig();
                        initResult(config.getDeviceStatus());
                        hint.set(handleError(throwable));
                        ToastManager.toast("脱机模式", ToastManager.INFO);
                    }
                });
    }

    private void initResult(String status) {
        if (TextUtils.equals(status, ValueUtil.DEVICE_ENABLE)) {
            hint.set("设备校验成功");
            initSuccess.setValue(Boolean.TRUE);
        } else {
            showErrorMessage("该设备已禁用，请联系管理员");
        }
    }

    private void showErrorMessage(String message) {
        Config config = ConfigManager.getInstance().getConfig();
        errorMode.set(Boolean.TRUE);
        hint.set(message + "\n" + "设备IMEI：" + config.getDeviceIMEI());
    }

}
