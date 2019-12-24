package com.miaxis.postal.viewModel;

import android.util.Log;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.model.CourierModel;
import com.miaxis.postal.data.repository.LoginRepository;
import com.miaxis.postal.util.ValueUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LoginViewModel extends BaseViewModel {

    public MutableLiveData<Courier> courierLiveData = new MutableLiveData<>();
    public ObservableBoolean editMode = new ObservableBoolean(false);
    public ObservableField<String> phone = new ObservableField<>();

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
                }, throwable -> {
                    waitMessage.setValue("");
                    throwable.printStackTrace();
                    Log.e("asd", "" + throwable.getMessage());
                    if (ValueUtil.isNetException(throwable)) {
                        resultMessage.setValue("联网错误");
                    } else if (throwable instanceof MyException) {
                        resultMessage.setValue(throwable.getMessage());
                    } else {
                        Log.e("asd", "" + throwable.getMessage());
                        resultMessage.setValue("出现错误");
                    }
                });
    }

}
