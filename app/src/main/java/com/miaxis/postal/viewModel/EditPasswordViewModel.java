package com.miaxis.postal.viewModel;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.repository.LoginRepository;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EditPasswordViewModel extends BaseViewModel {

    public ObservableField<String> oldPassword = new ObservableField<>();
    public ObservableField<String> newPassword = new ObservableField<>();
    public ObservableField<String> checkPassword = new ObservableField<>();

    public MutableLiveData<Boolean> editFlag = new SingleLiveEvent<>();

    public EditPasswordViewModel() {
    }

    public void editPassword(String password) {
        waitMessage.setValue("修改中，请稍后");
        Disposable subscribe = Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            LoginRepository.getInstance().editExpressmanSync(password);
            emitter.onNext(Boolean.TRUE);
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(courier -> {
                    waitMessage.setValue("");
                    resultMessage.setValue("修改成功");
                    editFlag.setValue(Boolean.TRUE);
                }, throwable -> {
                    waitMessage.setValue("");
                    resultMessage.setValue(handleError(throwable));
                });
    }

}
