package com.miaxis.postal.viewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.ValueUtil;

public class BaseViewModel extends ViewModel {

    public MutableLiveData<String> waitMessage = new MutableLiveData<>("");
    public MutableLiveData<String> resultMessage = new MutableLiveData<>("");
    public MutableLiveData<ToastManager.ToastBody> toast = new MutableLiveData<>();

    protected String hanleError(Throwable throwable) {
        throwable.printStackTrace();
        Log.e("asd", "" + throwable.getMessage());
        if (ValueUtil.isNetException(throwable)) {
            return "联网错误";
        } else if (throwable instanceof MyException) {
            return throwable.getMessage();
        } else {
            return "出现错误";
        }
    }

}
