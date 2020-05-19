package com.miaxis.postal.viewModel;

import android.text.TextUtils;
import android.util.Log;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.model.CourierModel;
import com.miaxis.postal.data.repository.LoginRepository;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.ValueUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginViewModel extends BaseViewModel {

    public MutableLiveData<Courier> courierLiveData = new MutableLiveData<>();
    public ObservableBoolean editMode = new ObservableBoolean(false);
    public ObservableField<String> phone = new ObservableField<>();
    public MutableLiveData<Boolean> loginFaceFlag = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> loginFingerFlag = new SingleLiveEvent<>();

    public LoginViewModel() {
        loadCourier();
    }

    private void loadCourier() {
        Disposable subscribe = Observable.create((ObservableOnSubscribe<Courier>) emitter -> {
            Courier courier = LoginRepository.getInstance().loadCourierSync();
            emitter.onNext(courier);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(courier -> {
                    courierLiveData.setValue(courier);
                    phone.set(courier.getPhone());
                    editMode.set(Boolean.FALSE);
                }, throwable -> {
                    Log.e("asd", "本地无缓存数据");
                    editMode.set(Boolean.TRUE);
                });
    }

    public void getCourierByPhone() {
        waitMessage.setValue("查询中，请稍后");
        Disposable subscribe = Observable.create((ObservableOnSubscribe<Courier>) emitter -> {
            Courier courier = LoginRepository.getInstance().getCourierByPhoneSync(phone.get());
            emitter.onNext(courier);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(CourierModel::saveCourier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(courier -> {
                    waitMessage.setValue("");
                    courierLiveData.setValue(courier);
                    startLogin(courier);
                }, throwable -> {
                    waitMessage.setValue("");
                    Courier courier = courierLiveData.getValue();
                    if (courier != null && TextUtils.equals(courier.getPhone(), phone.get())) {
                        startLogin(courier);
                        toast.setValue(ToastManager.getToastBody("离线登录", ToastManager.INFO));
                    } else {
                        toast.setValue(ToastManager.getToastBody(handleError(throwable), ToastManager.INFO));
                    }
                });
    }

    private void startLogin(Courier courier) {
        Config config = ConfigManager.getInstance().getConfig();
        if (config.getLoginMode() == ValueUtil.LOGIN_MODE_FINGER) {
            if (!TextUtils.isEmpty(courier.getFingerFeature1())
                    || !TextUtils.isEmpty(courier.getFingerFeature2())) {
                loginFingerFlag.setValue(Boolean.TRUE);
            } else {
                resultMessage.setValue("该账号下无指纹信息");
            }
        } else if (config.getLoginMode() == ValueUtil.LOGIN_MODE_FACE) {
            if (!TextUtils.isEmpty(courier.getFaceFeature())
                    || !TextUtils.isEmpty(courier.getMaskFaceFeature())) {
                loginFaceFlag.setValue(Boolean.TRUE);
            } else {
                resultMessage.setValue("该账号下无人脸信息");
            }
        }
    }

}
