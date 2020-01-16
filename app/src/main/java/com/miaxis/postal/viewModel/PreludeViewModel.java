package com.miaxis.postal.viewModel;

import android.Manifest;
import android.text.TextUtils;
import android.util.Log;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import com.miaxis.postal.app.PostalApp;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.net.ResponseEntity;
import com.miaxis.postal.data.repository.DeviceRepository;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.ValueUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
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
            Config config = ConfigManager.getInstance().getConfig();
            if (config.getDeviceId() == ValueUtil.DEFAULT_DEVICE_ID) {
                hint.set("初始化成功，首次使用，正在联网注册设备");
                registerDevice();
            } else {
                hint.set("初始化成功，正在查询设备状态");
                getDeviceStatus();
            }
        } else {
            errorMode.set(Boolean.TRUE);
            hint.set(message);
        }
    };

    private void registerDevice() {
        Disposable subscribe = Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            int deviceId = DeviceRepository.getInstance().registerDevice();
            emitter.onNext(deviceId);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(i -> {
                    Config config = ConfigManager.getInstance().getConfig();
                    config.setDeviceId(i);
                    ConfigManager.getInstance().saveConfigSync(config);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> {
                    hint.set("注册设备成功，正在查询设备状态");
                    getDeviceStatus();
                }, throwable -> {
                    errorMode.set(Boolean.TRUE);
                    hint.set(hanleError(throwable));
                });
    }

    private void getDeviceStatus() {
        Disposable subscribe = Observable.create((ObservableOnSubscribe<String>) emitter -> {
            String deviceStatus = DeviceRepository.getInstance().getDeviceStatus();
            emitter.onNext(deviceStatus);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(s -> {
                    Config config = ConfigManager.getInstance().getConfig();
                    config.setDeviceStatus(s);
                    ConfigManager.getInstance().saveConfigSync(config);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::initResult, throwable -> {
                    Config config = ConfigManager.getInstance().getConfig();
                    initResult(config.getDeviceStatus());
                    hint.set(hanleError(throwable));
                    ToastManager.toast("脱机模式", ToastManager.INFO);
                });
    }

    private void initResult(String status) {
        if (TextUtils.equals(status, ValueUtil.DEVICE_ENABLE)) {
            hint.set("设备校验成功");
            initSuccess.setValue(Boolean.TRUE);
        } else {
            Config config = ConfigManager.getInstance().getConfig();
            errorMode.set(Boolean.TRUE);
            hint.set("该设备已禁用，请联系管理员\n" + "设备MAC：" + config.getMac());
        }
    }

}
