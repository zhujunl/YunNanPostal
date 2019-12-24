package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.repository.LoginRepository;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RegisterViewModel extends BaseViewModel {

    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> number = new ObservableField<>();
    public ObservableField<String> phone = new ObservableField<>();

    public MutableLiveData<Boolean> registerFlag = new SingleLiveEvent<>();

    private String featureCache;
    private Bitmap headerCache;

    public RegisterViewModel() {
    }

    public boolean checkInput() {
        if (TextUtils.isEmpty(name.get())
                || TextUtils.isEmpty(number.get())
                || TextUtils.isEmpty(phone.get())
                || TextUtils.isEmpty(featureCache)
                || headerCache == null) {
            return false;
        }
        return true;
    }

    public void getCourierByPhone() {
        waitMessage.setValue("注册中，请稍后");
        Disposable subscribe = Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            LoginRepository.getInstance().registerExpressmanSync(name.get(),
                    number.get(),
                    phone.get(),
                    featureCache,
                    null,
                    null,
                    headerCache);
            emitter.onNext(Boolean.TRUE);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(courier -> {
                    waitMessage.setValue("");
                    resultMessage.setValue("注册成功");
                    registerFlag.setValue(Boolean.TRUE);
                }, throwable -> {
                    waitMessage.setValue("");
                    resultMessage.setValue(hanleError(throwable));
                });
    }

    public void setFeatureCache(String featureCache) {
        this.featureCache = featureCache;
    }

    public void setHeaderCache(Bitmap headerCache) {
        this.headerCache = headerCache;
    }
}
