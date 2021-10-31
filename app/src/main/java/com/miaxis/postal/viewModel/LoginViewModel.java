package com.miaxis.postal.viewModel;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.entity.DevicesStatusEntity;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.exception.NetResultFailedException;
import com.miaxis.postal.data.model.CourierModel;
import com.miaxis.postal.data.repository.DeviceStatusRepository;
import com.miaxis.postal.data.repository.LoginRepository;
import com.miaxis.postal.manager.DataCacheManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.EncryptUtil;
import com.miaxis.postal.util.SPUtils;
import com.miaxis.postal.util.ValueUtil;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginViewModel extends BaseViewModel {

    public MutableLiveData<Courier> courierLiveData = new MutableLiveData<>();
    public MutableLiveData<DevicesStatusEntity.DataDTO> devicesStatus = new MutableLiveData<>();
    public MutableLiveData<Boolean> loginResult = new SingleLiveEvent<>();

    public ObservableField<String> username = new ObservableField<>("");
    public ObservableField<String> password = new ObservableField<>("");

    public LoginViewModel() {
        loadCourier();
    }

    private void loadCourier() {
        DataCacheManager.getInstance().setCourier(null);
        Disposable subscribe = Observable.create((ObservableOnSubscribe<Courier>) emitter -> {
            Courier courier = LoginRepository.getInstance().loadCourierSync();
            emitter.onNext(courier);
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(courier -> {
                    courierLiveData.setValue(courier);
                    username.set(courier.getPhone());
                }, throwable -> {
                    Log.e("asd", "本地无缓存数据");
                });
    }

    public void getDevices(String macAddress) {
        Observable.create((ObservableOnSubscribe<DevicesStatusEntity.DataDTO>) emitter -> {
            DevicesStatusEntity.DataDTO devicesStatusEntity = DeviceStatusRepository.getInstance().getStatus(macAddress);
            emitter.onNext(devicesStatusEntity);
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(status -> {
                    devicesStatus.setValue(status);
                    //如果不是禁用状态则正常登录
                    boolean devices_enable = SPUtils.getInstance().read("devices_enable", false);
                    if (status.getStatus().equals(ValueUtil.DEVICE_ENABLE)) {
                        if (!devices_enable) {
                            ToastManager.toast("设备已被启用:" + status.getEnableRemark(), ToastManager.INFO, Toast.LENGTH_LONG);
                            SPUtils.getInstance().write("devices_enable", true);
                        }
                        getCourier(macAddress);
                    } else {
                        if (devices_enable) {
                            ToastManager.toast("设备已被禁用:" + status.getDisableRemark(), ToastManager.INFO, Toast.LENGTH_LONG);
                            SPUtils.getInstance().write("devices_enable", false);
                        }
                    }
                }, throwable -> {
                    devicesStatus.setValue(null);
                });
    }

    public void getCourier(String macAddress) {
        waitMessage.setValue("登录中，请稍后");
        Disposable subscribe = Observable.create((ObservableOnSubscribe<Courier>) emitter -> {
            Courier courier = LoginRepository.getInstance().getCourierByPhoneSync(username.get());
            emitter.onNext(courier);
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .doOnNext(CourierModel::saveCourier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(courier -> {
                    Observable.create((ObservableOnSubscribe<DevicesStatusEntity.DataDTO>) emitter -> {
                        DevicesStatusEntity.DataDTO devicesStatusEntity = DeviceStatusRepository.getInstance().getStatus(macAddress);
                        emitter.onNext(devicesStatusEntity);
                    }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(status -> {
                                devicesStatus.setValue(status);
                                //如果不是禁用状态则正常登录
                                if (status.getStatus().equals(ValueUtil.DEVICE_ENABLE)) {
                                    boolean devices_enable = SPUtils.getInstance().read("devices_enable", false);
                                    if (!devices_enable) {
                                        ToastManager.toast("设备已被启用:" + status.getEnableRemark(), ToastManager.INFO, Toast.LENGTH_LONG);
                                        SPUtils.getInstance().write("devices_enable", true);
                                    }
                                    waitMessage.setValue("");
                                    courierLiveData.setValue(courier);
                                    startLogin(courier);
                                } else {
                                    ToastManager.toast("设备已被禁用:" + status.getDisableRemark(), ToastManager.INFO, Toast.LENGTH_LONG);
                                    SPUtils.getInstance().write("devices_enable", false);
                                    waitMessage.setValue(null);
                                }
                            }, throwable -> {
                                waitMessage.setValue("");
                                devicesStatus.setValue(null);
                            });
                }, throwable -> {
                    waitMessage.setValue("");
                    if (throwable instanceof NetResultFailedException) {
                        toast.setValue(ToastManager.getToastBody(throwable.getMessage(), ToastManager.ERROR));
                    } else {
                        Courier courier = courierLiveData.getValue();
                        if (courier != null && TextUtils.equals(courier.getPhone(), username.get())) {
                            startLogin(courier);
                            toast.setValue(ToastManager.getToastBody("离线登录", ToastManager.INFO));
                        } else {
                            toast.setValue(ToastManager.getToastBody(handleError(throwable), ToastManager.INFO));
                        }
                    }
                });
    }

    private void startLogin(Courier courier) {
        try {
            if (TextUtils.equals(courier.getPassword(), getInputPasswordMD5())) {
                DataCacheManager.getInstance().setCourier(courier);
                loginResult.postValue(Boolean.TRUE);
                return;
            }
        } catch (MyException e) {
            e.printStackTrace();
        }
        loginResult.postValue(Boolean.FALSE);
    }

    private String getInputPasswordMD5() throws MyException {
        String passwordStr = password.get();
        if (!TextUtils.isEmpty(passwordStr)) {
            String passwordMD5 = EncryptUtil.md5Decode32(passwordStr);
            if (!TextUtils.isEmpty(passwordMD5)) {
                return passwordMD5;
            }
        }
        throw new MyException("输入密码为空或提取MD5失败");
    }

}
