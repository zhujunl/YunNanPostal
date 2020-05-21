package com.miaxis.postal.viewModel;

import android.text.TextUtils;
import android.util.Log;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.model.CourierModel;
import com.miaxis.postal.data.repository.LoginRepository;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.EncryptUtil;
import com.miaxis.postal.util.ValueUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginViewModel extends BaseViewModel {

    public MutableLiveData<Courier> courierLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> loginResult = new SingleLiveEvent<>();

    public ObservableField<String> username = new ObservableField<>("");
    public ObservableField<String> password = new ObservableField<>("");

    public LoginViewModel() {
        loadCourier();
    }

    private void loadCourier() {
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

    public void getCourier() {
        waitMessage.setValue("查询中，请稍后");
        Disposable subscribe = Observable.create((ObservableOnSubscribe<Courier>) emitter -> {
            Courier courier = LoginRepository.getInstance().getCourierByPhoneSync(username.get());
            emitter.onNext(courier);
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .doOnNext(CourierModel::saveCourier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(courier -> {
                    waitMessage.setValue("");
                    courierLiveData.setValue(courier);
                    startLogin(courier);
                }, throwable -> {
                    waitMessage.setValue("");
                    Courier courier = courierLiveData.getValue();
                    if (courier != null && TextUtils.equals(courier.getPhone(), username.get())) {
                        startLogin(courier);
                        toast.setValue(ToastManager.getToastBody("离线登录", ToastManager.INFO));
                    } else {
                        toast.setValue(ToastManager.getToastBody(handleError(throwable), ToastManager.INFO));
                    }
                });
    }

    private void startLogin(Courier courier) {
        try {
            if (TextUtils.equals(courier.getPassword(), getInputPasswordMD5())) {
                loginResult.postValue(Boolean.TRUE);
            }
        } catch (MyException e) {
            e.printStackTrace();
        }
        //TODO:
        loginResult.postValue(Boolean.TRUE);
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
